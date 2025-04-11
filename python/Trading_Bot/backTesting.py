import time
import ccxt
import pandas as pd
import talib
import matplotlib.pyplot as plt
from datetime import datetime, timedelta
import pytz

# Initialize Binance
exchange = ccxt.binance({
    'timeout': 30000,
    'enableRateLimit': True,
})

# Strategy Configuration - Updated for 1 year backtesting
config = {
    'symbol': 'BTC/USDT',
    'timeframe': '5m',
    'initial_balance': 200,
    'commission': 0.001,
    'rsi_period': 14,
    'ma_period': 9,
    # Set dates for 1 year period (adjust end date as needed)
    'backtest_start': (datetime.now() - timedelta(days=365)).strftime('%Y-%m-%d 00:00:00'),
    'backtest_end': datetime.now().strftime('%Y-%m-%d 23:59:59'),
}

def fetch_historical_data():
    """Fetch historical OHLCV data with improved date handling"""
    print(f"Fetching 1 year of historical data from {config['backtest_start']} to {config['backtest_end']}")
    all_ohlcv = []
    since = int(pd.Timestamp(config['backtest_start']).timestamp() * 1000)
    end_date = int(pd.Timestamp(config['backtest_end']).timestamp() * 1000)
    # Ensure parentheses are properly closed    
    while since < end_date:
        try:
            data = exchange.fetch_ohlcv(
                config['symbol'], 
                config['timeframe'], 
                since=since, 
                limit=1000
            )
            if not data:
                break
            since = data[-1][0] + 1  # Next candle
            all_ohlcv += data
            
            # Progress indicator
            current_date = pd.to_datetime(data[-1][0], unit='ms')
            print(f"Fetched data up to: {current_date}", end='\r')
            
        except Exception as e:
            print(f"\nError fetching data: {e}")
            time.sleep(5)
            continue
    
    df = pd.DataFrame(all_ohlcv, columns=['timestamp', 'open', 'high', 'low', 'close', 'volume'])
    df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')
    df = df[(df['timestamp'] >= pd.Timestamp(config['backtest_start'])) & 
            (df['timestamp'] <= pd.Timestamp(config['backtest_end']))]
    print("\nData fetch complete!")
    return df

def calculate_indicators(df):
    """Calculate technical indicators with progress tracking"""
    print("Calculating indicators...")
    df['rsi'] = talib.RSI(df['close'], timeperiod=config['rsi_period'])
    df['rsi_ma'] = df['rsi'].rolling(window=config['ma_period']).mean()
    return df.dropna()

def backtest(df):
    """Run the backtest with progress tracking"""
    print("Running backtest...")
    balance = config['initial_balance']
    position = 0
    trades = []
    equity_curve = []
    buy_price = 0
    
    total_candles = len(df)
    for i in range(config['ma_period'], total_candles):
        current = df.iloc[i]
        prev_rsi = df['rsi'].iloc[i-1]
        prev_ma = df['rsi_ma'].iloc[i-1]
        current_rsi = current['rsi']
        current_ma = current['rsi_ma']
        
        # Track equity curve
        current_equity = balance + (position * current['close'] * (1 - config['commission']))
        equity_curve.append((current['timestamp'], current_equity))
        
        # Progress indicator
        if i % 1000 == 0 or i == total_candles - 1:
            print(f"Processing candle {i+1}/{total_candles} ({current['timestamp']})", end='\r')
        
        # Buy Signal (RSI crosses above MA)
        if (prev_rsi < prev_ma) and (current_rsi > current_ma) and position == 0:
            buy_price = current['close']
            position = balance / buy_price  # Use all available balance
            balance = 0
            trades.append({
                'type': 'buy',
                'timestamp': current['timestamp'],
                'price': buy_price,
                'position_size': position
            })
        
        # Sell Signal (RSI crosses below MA)
        elif (prev_rsi > prev_ma) and (current_rsi < current_ma) and position > 0:
            sell_price = current['close']
            balance = position * sell_price * (1 - config['commission'])
            profit_loss = (sell_price - buy_price) * position
            trades.append({
                'type': 'sell',
                'timestamp': current['timestamp'],
                'price': sell_price,
                'profit_loss': profit_loss,
                'return_pct': (sell_price - buy_price) / buy_price * 100,
                'position_size': position
            })
            position = 0
    
    # Close any open position at the end
    if position > 0:
        sell_price = df.iloc[-1]['close']
        balance = position * sell_price * (1 - config['commission'])
        profit_loss = (sell_price - buy_price) * position
        trades.append({
            'type': 'sell',
            'timestamp': df.iloc[-1]['timestamp'],
            'price': sell_price,
            'profit_loss': profit_loss,
            'return_pct': (sell_price - buy_price) / buy_price * 100,
            'position_size': position
        })
    
    # Create DataFrames
    equity_df = pd.DataFrame(equity_curve, columns=['timestamp', 'equity'])
    equity_df.set_index('timestamp', inplace=True)
    trades_df = pd.DataFrame(trades)
    
    print("\nBacktest complete!")
    return balance, trades_df, equity_df

def visualize_results(df, trades_df, equity_df):
    """Enhanced visualizations for 1 year data"""
    plt.figure(figsize=(16, 10))
    
    # Price and trades plot
    plt.subplot(2, 1, 1)
    plt.plot(df['timestamp'], df['close'], label='Price', color='blue', alpha=0.8)
    
    # Plot trades with more visible markers
    buy_trades = trades_df[trades_df['type'] == 'buy']
    sell_trades = trades_df[trades_df['type'] == 'sell']
    
    plt.scatter(buy_trades['timestamp'], buy_trades['price'], 
               color='green', marker='^', s=80, label='Buy', alpha=0.9)
    plt.scatter(sell_trades['timestamp'], sell_trades['price'], 
               color='red', marker='v', s=80, label='Sell', alpha=0.9)
    
    plt.title(f'{config["symbol"]} Price and Trades (1 Year)', fontsize=12)
    plt.legend()
    plt.grid(True, alpha=0.3)
    
    # Enhanced equity curve plot
    plt.subplot(2, 1, 2)
    plt.plot(equity_df.index, equity_df['equity'], 
            label='Equity', color='green', linewidth=2)
    
    # Mark initial and final balance
    plt.axhline(y=config['initial_balance'], color='gray', linestyle='--', alpha=0.5)
    final_balance = equity_df['equity'].iloc[-1]
    plt.axhline(y=final_balance, color='blue', linestyle='--', alpha=0.5)
    
    plt.title(f'Equity Curve (Final: ${final_balance:.2f})', fontsize=12)
    plt.legend()
    plt.grid(True, alpha=0.3)
    
    plt.tight_layout()
    plt.show()

def print_metrics(trades_df, equity_df):
    """Enhanced performance metrics for 1 year"""
    initial_balance = config['initial_balance']
    final_balance = equity_df['equity'].iloc[-1]
    total_return = ((final_balance - initial_balance) / initial_balance) * 100
    
    sell_trades = trades_df[trades_df['type'] == 'sell']
    winning_trades = len(sell_trades[sell_trades['profit_loss'] > 0])
    losing_trades = len(sell_trades[sell_trades['profit_loss'] <= 0])
    win_rate = winning_trades / len(sell_trades) if len(sell_trades) > 0 else 0
    
    # Calculate average win/loss
    avg_win = sell_trades[sell_trades['profit_loss'] > 0]['profit_loss'].mean() if winning_trades > 0 else 0
    avg_loss = sell_trades[sell_trades['profit_loss'] <= 0]['profit_loss'].mean() if losing_trades > 0 else 0
    
    print("\n=== 1 Year Backtest Results ===")
    print(f"Period: {config['backtest_start']} to {config['backtest_end']}")
    print(f"Initial Balance: ${initial_balance:.2f}")
    print(f"Final Balance: ${final_balance:.2f}")
    print(f"Total Return: {total_return:.2f}%")
    print(f"\n=== Trade Statistics ===")
    print(f"Total Trades: {len(sell_trades)}")
    print(f"Winning Trades: {winning_trades} ({win_rate*100:.1f}%)")
    print(f"Losing Trades: {losing_trades} ({(1-win_rate)*100:.1f}%)")
    print(f"Average Win: ${avg_win:.2f}")
    print(f"Average Loss: ${avg_loss:.2f}")
    print(f"Profit Factor: {abs(avg_win * winning_trades / (abs(avg_loss) * losing_trades)):.2f}" 
          if losing_trades > 0 else "N/A")

def main():
    """Main execution function"""
    # Fetch and prepare data
    df = fetch_historical_data()
    df = calculate_indicators(df)
    
    # Run backtest
    final_balance, trades_df, equity_df = backtest(df)
    
    # Print results
    print_metrics(trades_df, equity_df)
    
    # Visualize results
    visualize_results(df, trades_df, equity_df)

if __name__ == "__main__":
    main()