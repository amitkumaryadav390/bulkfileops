import React from 'react';

const Header = () => {
  return (
    <header className="header">
      <div className="container">
        <div className="logo">
          <i className="fas fa-file-word"></i>
          <span>FileCreator</span>
        </div>
        <nav className="nav">
          <a href="#features" className="nav-link">Features</a>
          <a href="#docs" className="nav-link">Documentation</a>
          <a href="#support" className="nav-link">Support</a>
        </nav>
      </div>
    </header>
  );
};

export default Header;