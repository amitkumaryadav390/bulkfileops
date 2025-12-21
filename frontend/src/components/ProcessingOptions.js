import React from 'react';
import axios from 'axios';

const ProcessingOptions = ({ 
  processingType, 
  onProcessingTypeChange, 
  uploadedFile, 
  setIsProcessing, 
  setResult, 
  setError,
  isProcessing 
}) => {

  const processFile = async (type) => {
    setIsProcessing(true);
    setError(null);
    setResult(null);

    const formData = new FormData();
    formData.append('file', uploadedFile);

    try {
      const endpoint = type === 'aggregated' 
        ? 'http://localhost:8080/api/generate-aggregated-docs' 
        : 'http://localhost:8080/api/generate-docs';

      const response = await axios.post(endpoint, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        responseType: 'blob', // Important for file download
      });

      // Create download link
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const filename = type === 'aggregated' 
        ? 'aggregated_documents.zip' 
        : 'individual_documents.zip';

      setResult({
        downloadUrl: url,
        filename: filename,
        type: type,
        size: response.data.size
      });

    } catch (error) {
      console.error('Processing error:', error);
      if (error.response?.status === 404) {
        setError('API endpoint not found. Please ensure the Spring Boot server is running on http://localhost:8080');
      } else if (error.code === 'ERR_NETWORK') {
        setError('Cannot connect to server. Please ensure the Spring Boot application is running.');
      } else {
        setError(error.response?.data?.message || error.message || 'Failed to process file');
      }
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="processing-options">
      <div className="option-types">
        <h4>Choose Processing Type:</h4>
        
        <div className="radio-group">
          <label className={`radio-option ${processingType === 'individual' ? 'selected' : ''}`}>
            <input
              type="radio"
              name="processingType"
              value="individual"
              checked={processingType === 'individual'}
              onChange={(e) => onProcessingTypeChange(e.target.value)}
              disabled={isProcessing}
            />
            <div className="radio-content">
              <div className="radio-header">
                <i className="fas fa-file-alt"></i>
                <span>Individual Documents</span>
              </div>
              <p>Generate one Word document for each row in your Excel/CSV file</p>
            </div>
          </label>

          <label className={`radio-option ${processingType === 'aggregated' ? 'selected' : ''}`}>
            <input
              type="radio"
              name="processingType"
              value="aggregated"
              checked={processingType === 'aggregated'}
              onChange={(e) => onProcessingTypeChange(e.target.value)}
              disabled={isProcessing}
            />
            <div className="radio-content">
              <div className="radio-header">
                <i className="fas fa-layer-group"></i>
                <span>Aggregated Documents</span>
              </div>
              <p>Group by importer name and generate combined documents with totaled amounts</p>
            </div>
          </label>
        </div>
      </div>

      <div className="processing-details">
        {processingType === 'individual' ? (
          <div className="details-card">
            <h5><i className="fas fa-info-circle"></i> Individual Processing</h5>
            <ul>
              <li>Creates one document per Excel/CSV row</li>
              <li>Each document contains data from a single record</li>
              <li>Perfect for individual letters or certificates</li>
              <li>Preserves original data structure</li>
            </ul>
          </div>
        ) : (
          <div className="details-card">
            <h5><i className="fas fa-info-circle"></i> Aggregated Processing</h5>
            <ul>
              <li>Groups records by importer name</li>
              <li>Sums differential duty amounts</li>
              <li>Combines HS codes and descriptions with commas</li>
              <li>Uses first address found for each importer</li>
              <li>Perfect for summary reports</li>
            </ul>
          </div>
        )}
      </div>

      <div className="action-buttons">
        <button
          className={`process-button ${isProcessing ? 'processing' : ''}`}
          onClick={() => processFile(processingType)}
          disabled={!uploadedFile || isProcessing}
        >
          {isProcessing ? (
            <>
              <i className="fas fa-spinner fa-spin"></i>
              Processing...
            </>
          ) : (
            <>
              <i className="fas fa-cogs"></i>
              Generate Documents
            </>
          )}
        </button>
      </div>
    </div>
  );
};

export default ProcessingOptions;