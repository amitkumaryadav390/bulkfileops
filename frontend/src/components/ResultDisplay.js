import React from 'react';

const ResultDisplay = ({ result, error, onReset }) => {
  const handleDownload = () => {
    if (result?.downloadUrl) {
      const link = document.createElement('a');
      link.href = result.downloadUrl;
      link.download = result.filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return 'Unknown size';
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  return (
    <div className="result-display">
      {error && (
        <div className="error-card">
          <div className="error-header">
            <i className="fas fa-exclamation-triangle"></i>
            <h4>Processing Failed</h4>
          </div>
          <p className="error-message">{error}</p>
          <div className="error-actions">
            <button className="retry-button" onClick={onReset}>
              <i className="fas fa-redo"></i>
              Try Again
            </button>
          </div>
          
          <div className="troubleshooting">
            <h5>Troubleshooting:</h5>
            <ul>
              <li>Ensure the Spring Boot server is running on http://localhost:8080</li>
              <li>Check that your file has the correct format and column headers</li>
              <li>Make sure the Rough.docx template file exists in the project root</li>
              <li>Verify your file size is under 10 MB</li>
            </ul>
          </div>
        </div>
      )}

      {result && (
        <div className="success-card">
          <div className="success-header">
            <i className="fas fa-check-circle"></i>
            <h4>Processing Completed Successfully!</h4>
          </div>
          
          <div className="result-details">
            <div className="file-info">
              <i className="fas fa-file-archive"></i>
              <div className="file-meta">
                <h5>{result.filename}</h5>
                <p>{formatFileSize(result.size)}</p>
                <span className="processing-type">
                  {result.type === 'aggregated' ? 'Aggregated Documents' : 'Individual Documents'}
                </span>
              </div>
            </div>
            
            <div className="result-actions">
              <button className="download-button" onClick={handleDownload}>
                <i className="fas fa-download"></i>
                Download ZIP File
              </button>
              
              <button className="reset-button" onClick={onReset}>
                <i className="fas fa-upload"></i>
                Process Another File
              </button>
            </div>
          </div>
          
          <div className="next-steps">
            <h5>Next Steps:</h5>
            <ol>
              <li>Click "Download ZIP File" to save the generated documents</li>
              <li>Extract the ZIP file to access individual Word documents</li>
              <li>Each document will have placeholders replaced with your data</li>
              {result.type === 'aggregated' && (
                <li>Aggregated documents combine multiple rows per importer</li>
              )}
            </ol>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResultDisplay;