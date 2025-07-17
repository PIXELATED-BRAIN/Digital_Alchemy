const express = require('express');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const bcrypt = require('bcryptjs');
const session = require('express-session');

const app = express();
const db = new sqlite3.Database('./delivery.db');

// Middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));
app.use(session({
  secret: 'your-secret-key',
  resave: false,
  saveUninitialized: true,
  cookie: { secure: false } // Set to true if using HTTPS
}));

// Database migration function
const migrateDatabase = () => {
  return new Promise((resolve, reject) => {
    db.serialize(() => {
      // Create tables if they don't exist
      db.run(`
        CREATE TABLE IF NOT EXISTS shipments (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          tracking_number TEXT UNIQUE,
          status TEXT CHECK(status IN ('Processing', 'In Transit', 'Out for Delivery', 'Delivered')),
          recipient_name TEXT,
          delivery_address TEXT,
          package_type TEXT,
          origin TEXT DEFAULT 'Main Warehouse',
          destination TEXT,
          last_scan TEXT,
          estimated_delivery TEXT,
          created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
      `);
      
      db.run(`
        CREATE TABLE IF NOT EXISTS admins (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          username TEXT UNIQUE,
          password TEXT
        )
      `);
      
      // Add missing columns if needed
      db.run(`
        ALTER TABLE shipments ADD COLUMN recipient_name TEXT
      `, (err) => { if (err) console.log('recipient_name already exists') });
      
      db.run(`
        ALTER TABLE shipments ADD COLUMN delivery_address TEXT
      `, (err) => { if (err) console.log('delivery_address already exists') });
      
      db.run(`
        ALTER TABLE shipments ADD COLUMN package_type TEXT
      `, (err) => { if (err) console.log('package_type already exists') });
      
      resolve();
    });
  });
};

// Initialize database
migrateDatabase().then(() => {
  console.log('Database migration complete');
});

// Generate tracking number
function generateTrackingNumber() {
  return 'AMA-' + Math.floor(100000 + Math.random() * 900000);
}

// Authentication middleware
function requireAuth(req, res, next) {
  if (req.session && req.session.admin) {
    return next();
  }
  res.status(401).json({ error: 'Unauthorized' });
}

// Routes
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.get('/admin', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'admin.html'));
});

// Tracking API
app.get('/api/track/:trackingNumber', (req, res) => {
  const { trackingNumber } = req.params;
  
  db.get(
    `SELECT * FROM shipments WHERE tracking_number = ?`,
    [trackingNumber],
    (err, row) => {
      if (err) return res.status(500).json({ error: err.message });
      row ? res.json(row) : res.status(404).json({ error: "Tracking number not found" });
    }
  );
});

// Admin API
app.post('/api/admin/login', (req, res) => {
  const { username, password } = req.body;
  
  db.get(
    `SELECT * FROM admins WHERE username = ?`,
    [username],
    (err, admin) => {
      if (err) return res.status(500).json({ error: err.message });
      if (!admin) return res.status(401).json({ error: "Invalid credentials" });
      
      bcrypt.compare(password, admin.password, (err, result) => {
        if (err || !result) {
          return res.status(401).json({ error: "Invalid credentials" });
        }
        
        req.session.admin = { id: admin.id, username: admin.username };
        res.json({ success: true });
      });
    }
  );
});

app.post('/api/admin/create-delivery', requireAuth, (req, res) => {
  const { recipient_name, delivery_address, package_type } = req.body;
  const tracking_number = generateTrackingNumber();
  const origin = 'Main Warehouse';
  const destination = delivery_address.split(',').slice(-2)[0].trim() || delivery_address;
  
  db.run(
    `INSERT INTO shipments (
      tracking_number, status, recipient_name, 
      delivery_address, package_type, origin, 
      destination, last_scan, estimated_delivery
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [
      tracking_number,
      'Processing',
      recipient_name,
      delivery_address,
      package_type,
      origin,
      destination,
      new Date().toISOString(),
      new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    ],
    function(err) {
      if (err) return res.status(400).json({ error: err.message });
      res.json({ 
        success: true, 
        tracking_number,
        id: this.lastID 
      });
    }
  );
});

app.post('/api/admin/update-status', requireAuth, (req, res) => {
  const { tracking_number, status } = req.body;
  
  db.run(
    `UPDATE shipments SET status = ?, last_scan = ? WHERE tracking_number = ?`,
    [status, new Date().toISOString(), tracking_number],
    function(err) {
      if (err) return res.status(400).json({ error: err.message });
      res.json({ success: true, changes: this.changes });
    }
  );
});

app.get('/api/admin/deliveries', requireAuth, (req, res) => {
  db.all(
    `SELECT * FROM shipments ORDER BY created_at DESC`,
    [],
    (err, rows) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json(rows);
    }
  );
});

app.post('/api/admin/logout', (req, res) => {
  req.session.destroy(err => {
    if (err) {
      return res.status(500).json({ error: 'Could not log out, please try again' });
    }
    res.json({ success: true });
  });
});

// Add this to your server.js after the database setup
app.post('/api/admin/init', (req, res) => {
    const { username, password } = req.body;
    const hashedPassword = bcrypt.hashSync(password, 10);
    
    db.run(
        `INSERT INTO admins (username, password) VALUES (?, ?)`,
        [username, hashedPassword],
        function(err) {
            if (err) return res.status(400).json({ error: err.message });
            res.json({ success: true });
        }
    );
});

// Add this authentication check endpoint
app.get('/api/admin/check-auth', (req, res) => {
    res.json({ authenticated: !!(req.session && req.session.admin) });
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));