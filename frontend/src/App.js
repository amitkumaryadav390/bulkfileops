import React, { useState } from 'react';
import FileUpload from './components/FileUpload';
import ProcessingOptions from './components/ProcessingOptions';
import ResultDisplay from './components/ResultDisplay';
import Header from './components/Header';
import Footer from './components/Footer';
import './styles/App.css';

const App = () => {
  const [uploadedFile, setUploadedFile] = useState(null);
  const [processingType, setProcessingType] = useState('individual');
  const [isProcessing, setIsProcessing] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const handleFileUpload = (file) => {
    setUploadedFile(file);
    setResult(null);
    setError(null);
  };

  const handleProcessingTypeChange = (type) => {
    setProcessingType(type);
    setResult(null);
    setError(null);
  };

  const handleReset = () => {
    setUploadedFile(null);
    setResult(null);
    setError(null);
    setProcessingType('individual');
    setIsProcessing(false);
  };

  return (
    <div className="app">
      <Header />
      
      <main className="main-content">
        <div className="container">
          <div className="hero-section">
            <h1>Excel to Word Document Generator</h1>
            <p>Upload your Excel or CSV file and generate professional Word documents with custom templates</p>
          </div>

          <div className="workflow-container">
            {/* Step 1: File Upload */}
            <div className="step-card">
              <div className="step-header">
                <div className="step-number">1</div>
                <h3>Upload File</h3>
              </div>
              <FileUpload 
                onFileUpload={handleFileUpload} 
                uploadedFile={uploadedFile}
                isProcessing={isProcessing}
              />
            </div>

            {/* Step 2: Processing Options */}
            {uploadedFile && (
              <div className="step-card">
                <div className="step-header">
                  <div className="step-number">2</div>
                  <h3>Processing Options</h3>
                </div>
                <ProcessingOptions
                  processingType={processingType}
                  onProcessingTypeChange={handleProcessingTypeChange}
                  uploadedFile={uploadedFile}
                  setIsProcessing={setIsProcessing}
                  setResult={setResult}
                  setError={setError}
                  isProcessing={isProcessing}
                />
              </div>
            )}

            {/* Step 3: Results */}
            {(result || error) && (
              <div className="step-card">
                <div className="step-header">
                  <div className="step-number">3</div>
                  <h3>Results</h3>
                </div>
                <ResultDisplay 
                  result={result}
                  error={error}
                  onReset={handleReset}
                />
              </div>
            )}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default App;