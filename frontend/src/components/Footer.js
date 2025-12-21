import React from 'react';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-content">
          <div className="footer-section">
            <h4>FileCreator</h4>
            <p>Transform your Excel data into professional Word documents with ease.</p>
          </div>
          
          <div className="footer-section">
            <h5>Features</h5>
            <ul>
              <li>Excel/CSV Upload</li>
              <li>Template-based Generation</li>
              <li>Individual & Aggregated Docs</li>
              <li>Bulk Processing</li>
            </ul>
          </div>
          
          <div className="footer-section">
            <h5>Support</h5>
            <ul>
              <li>Documentation</li>
              <li>API Reference</li>
              <li>Template Guide</li>
              <li>Troubleshooting</li>
            </ul>
          </div>
          
          <div className="footer-section">
            <h5>Connect</h5>
            <div className="social-links">
              <a href="#" className="social-link">
                <i className="fab fa-github"></i>
              </a>
              <a href="#" className="social-link">
                <i className="fab fa-linkedin"></i>
              </a>
              <a href="#" className="social-link">
                <i className="fas fa-envelope"></i>
              </a>
            </div>
          </div>
        </div>
        
        <div className="footer-bottom">
          <p>&copy; 2025 FileCreator. Built with React & Spring Boot.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;