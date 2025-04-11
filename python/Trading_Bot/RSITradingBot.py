import time
import ccxt
import pandas as pd
import talib
from datetime import datetime, timedelta
import pytz

# Initialize Binance with longer timeout and rate limiting
exchange = ccxt.binance({
    'timeout': 30000,  # 30 seconds timeout (in milliseconds)
    'enableRateLimit': True,  # Respect Binance's rate limits
})

symbol = 'BTC/USDT'
timeframe = '5m'
rsi_period = 14
ma_period = 9  # Moving average period for RSI
last_trade_time = None
position_open = False  # Track if a position is open
ny_timezone = pytz.timezone('America/New_York')

def fetch_ohlcv(max_retries=3, retry_delay=5):
    for _ in range(max_retries):
        try:
            data = exchange.fetch_ohlcv(symbol, timeframe)
            df = pd.DataFrame(data, columns=['timestamp', 'open', 'high', 'low', 'close', 'volume'])
            df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')
            df['timestamp'] = df['timestamp'].dt.tz_localize(pytz.utc).dt.tz_convert(ny_timezone)
            return df
        except (ccxt.RequestTimeout, ccxt.NetworkError) as e:
            print(f"Error fetching data: {e}. Retrying in {retry_delay} seconds...")
            time.sleep(retry_delay)
    raise Exception("Failed to fetch OHLCV data after retries.")

# Function to calculate RSI and its moving average
def calculate_rsi(df):
    df['rsi'] = talib.RSI(df['close'], timeperiod=rsi_period)
    df['rsi_ma'] = df['rsi'].rolling(window=ma_period).mean()  # Compute RSI MA
    return df

# Function to check trade conditions (RSI-MA crossover + position handling)
def check_trade(df):
    global last_trade_time, position_open
    
    if len(df) < ma_period + 1:  # Ensure enough data for MA calculation
        return None
    
    # Get current and previous RSI & MA values
    prev_rsi = df['rsi'].iloc[-2]
    prev_ma = df['rsi_ma'].iloc[-2]
    current_rsi = df['rsi'].iloc[-1]
    current_ma = df['rsi_ma'].iloc[-1]
    
    if not position_open:
        # Enter position when RSI crosses above MA
        if prev_rsi < prev_ma and current_rsi > current_ma:
            print("✅ ENTER TRADE: RSI crossed above MA")
            position_open = True
            return 'buy'
    else:
        # Exit position when RSI crosses below MA
        if prev_rsi > prev_ma and current_rsi < current_ma:
            print("❌ EXIT TRADE: RSI crossed below MA")
            position_open = False
            return 'sell'
    
    return None

# Function to place an order (Disabled for safety)
def place_order(order_type):
    print(f"Placing {order_type} order for {symbol}")
    # Uncomment the next line to execute real trades
    # exchange.create_market_order(symbol, order_type, amount)

def wait_for_next_candle():
    now = datetime.now(pytz.utc)
    seconds_until_next_candle = 300 - (now.timestamp() % 300)  # 300s = 5 min
    print(f"Sleeping for {seconds_until_next_candle:.2f} seconds until next 5m candle...")
    time.sleep(seconds_until_next_candle)

# Main loop
while True:
    try:
        df = fetch_ohlcv()
        df = calculate_rsi(df)
        trade_signal = check_trade(df)
        
        if trade_signal:
            place_order(trade_signal)
        
        print(f"Current RSI: {df['rsi'].iloc[-1]:.2f}, RSI MA: {df['rsi_ma'].iloc[-1]:.2f}")
        
        # Wait for the next candle before running again
        wait_for_next_candle()

    except Exception as e:
        print(f"Error in main loop: {e}. Retrying in 10 seconds...")
        time.sleep(10)
