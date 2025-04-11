import React from 'react';
import './HomePage.css';

function HomePage() {
  return (
    <div>
      <nav>
        <ul>
          <li><a href="#home">Home</a></li>
          <li><a href="#about">About</a></li>
          <li><a href="#projects">Projects</a></li>
          <li><a href="#contact">Contact</a></li>
        </ul>
      </nav>
      <header className="hero-section">
        <img src="path/to/your/photo.jpg" alt="Your Name" className="hero-photo" />
        <div className="hero-description">
          <h1>Your Name</h1>
          <p>Short description about yourself.</p>
        </div>
      </header>
    </div>
  );
}

export default HomePage;