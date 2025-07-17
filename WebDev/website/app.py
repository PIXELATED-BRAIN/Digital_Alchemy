from flask import Flask, render_template, request, redirect, url_for

app = Flask(__name__)

# Mock database for shipments
shipments = {
    "SH123456789": {
        "status": "In Transit",
        "origin": "Dubai, UAE",
        "destination": "New York, USA",
        "last_update": "2023-08-15 14:30",
        "progress": [
            {"date": "2023-08-10 09:00", "location": "Dubai Sorting Facility", "status": "Shipment picked up"},
            {"date": "2023-08-12 11:30", "location": "Dubai International Airport", "status": "Departed origin"},
            {"date": "2023-08-15 08:45", "location": "New York Customs", "status": "Arrived at destination country"},
        ]
    },
    "SH987654321": {
        "status": "Delivered",
        "origin": "London, UK",
        "destination": "Sydney, Australia",
        "last_update": "2023-08-05 10:15",
        "progress": [
            {"date": "2023-08-01 14:00", "location": "London Hub", "status": "Shipment picked up"},
            {"date": "2023-08-02 16:30", "location": "Heathrow Airport", "status": "Departed origin"},
            {"date": "2023-08-04 09:15", "location": "Sydney Sorting Facility", "status": "Arrived at destination"},
            {"date": "2023-08-05 10:15", "location": "Sydney Office", "status": "Delivered"},
        ]
    }
}

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/track', methods=['GET', 'POST'])
def track_shipment():
    tracking_number = None
    shipment_data = None
    
    if request.method == 'POST':
        tracking_number = request.form.get('tracking_number')
        shipment_data = shipments.get(tracking_number.upper())
    
    return render_template('track.html', 
                           tracking_number=tracking_number, 
                           shipment_data=shipment_data)

@app.route('/services')
def services():
    return render_template('services.html')

@app.route('/contact')
def contact():
    return render_template('contact.html')

@app.route('/about')
def about():
    return render_template('about.html')

if __name__ == '__main__':
    app.run(debug=True)