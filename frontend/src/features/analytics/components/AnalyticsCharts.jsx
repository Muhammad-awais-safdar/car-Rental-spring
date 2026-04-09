import React from "react";
import { Line, Bar, Pie, Doughnut } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import {
  lineChartOptions,
  barChartOptions,
  pieChartOptions,
} from "../utils/chartUtils";

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

export const LineChart = ({ data, options = {} }) => {
  const mergedOptions = { ...lineChartOptions, ...options };
  return <Line data={data} options={mergedOptions} />;
};

export const BarChart = ({ data, options = {} }) => {
  const mergedOptions = { ...barChartOptions, ...options };
  return <Bar data={data} options={mergedOptions} />;
};

export const PieChart = ({ data, options = {} }) => {
  const mergedOptions = { ...pieChartOptions, ...options };
  return <Pie data={data} options={mergedOptions} />;
};

export const DoughnutChart = ({ data, options = {} }) => {
  const mergedOptions = { ...pieChartOptions, ...options };
  return <Doughnut data={data} options={mergedOptions} />;
};

const AnalyticsCharts = {
  LineChart,
  BarChart,
  PieChart,
  DoughnutChart,
};

export default AnalyticsCharts;
