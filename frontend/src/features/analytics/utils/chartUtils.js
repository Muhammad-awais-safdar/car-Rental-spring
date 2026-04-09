// Chart.js default configurations and utilities

export const defaultChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
    },
  },
};

export const lineChartOptions = {
  ...defaultChartOptions,
  scales: {
    y: {
      beginAtZero: true,
    },
  },
};

export const barChartOptions = {
  ...defaultChartOptions,
  scales: {
    y: {
      beginAtZero: true,
    },
  },
};

export const pieChartOptions = {
  ...defaultChartOptions,
  plugins: {
    legend: {
      position: 'right',
    },
  },
};

// Color schemes
export const colorSchemes = {
  primary: [
    'rgba(59, 130, 246, 0.8)',   // blue
    'rgba(16, 185, 129, 0.8)',   // green
    'rgba(245, 158, 11, 0.8)',   // yellow
    'rgba(239, 68, 68, 0.8)',    // red
    'rgba(139, 92, 246, 0.8)',   // purple
    'rgba(236, 72, 153, 0.8)',   // pink
  ],
  borders: [
    'rgba(59, 130, 246, 1)',
    'rgba(16, 185, 129, 1)',
    'rgba(245, 158, 11, 1)',
    'rgba(239, 68, 68, 1)',
    'rgba(139, 92, 246, 1)',
    'rgba(236, 72, 153, 1)',
  ],
};

// Format number with commas
export const formatNumber = (num) => {
  return new Intl.NumberFormat('en-US').format(num);
};

// Format currency
export const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};

// Format percentage
export const formatPercentage = (value) => {
  return `${value.toFixed(2)}%`;
};

// Prepare chart data
export const prepareChartData = (labels, datasets) => {
  return {
    labels,
    datasets: datasets.map((dataset, index) => ({
      ...dataset,
      backgroundColor: dataset.backgroundColor || colorSchemes.primary[index % colorSchemes.primary.length],
      borderColor: dataset.borderColor || colorSchemes.borders[index % colorSchemes.borders.length],
      borderWidth: dataset.borderWidth || 2,
    })),
  };
};
