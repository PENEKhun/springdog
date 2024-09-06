/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const createTooltipCallback = (label) => (context) => `${context.parsed.y.toFixed(
    2)} ${label}`;

const createScales = (xTitle, yTitle, extraConfig) => ({
  x: {
    type: 'time',
    time: {unit: 'day'},
    title: {display: true, text: xTitle}
  },
  y: {
    beginAtZero: true,
    title: {display: true, text: yTitle},
    ...extraConfig
  }
});

const createChartConfig = (type, labels, datasets, options) => ({
  type,
  data: {labels, datasets},
  options: {
    responsive: true,
    maintainAspectRatio: false,
    ...options
  },
  plugins: {
    ...options.plugins,
    noDataMessage: {
      message: 'No data available',
      font: { size: 16 },
      color: 'rgba(100, 100, 100, 0.8)'
    }
  }
});

// Data filtering function
function filterData(data) {
  return data.filter(value => value !== -1 && value !== null && !isNaN(value));
}

// Chart creation functions
function createEndpointChart(elementId, label, data, yAxisLabel) {
  const ctx = document.getElementById(elementId).getContext('2d');

  const filteredData = filterData(data);

  const config = createChartConfig('line',
      recentEndpointMetrics.map(item => item.date),
      [{
        label: label,
        data: filteredData,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1
      }],
      {
        scales: createScales('Date', yAxisLabel),
        plugins: {
          legend: {display: false},
          tooltip: {callbacks: {label: createTooltipCallback(yAxisLabel)}}
        }
      }
  );

  new Chart(ctx, config);
}

function createChart(elementId, label, data, yAxisLabel, extraConfig) {
  const ctx = document.getElementById(elementId).getContext('2d');

  const filteredData = filterData(data);

  const config = createChartConfig('line',
      systemMetrics.map(metric => metric.timestamp),
      [{
        label: label,
        data: filteredData,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1
      }],
      {
        scales: createScales('Time', yAxisLabel, extraConfig),
        plugins: {
          legend: {display: false},
          tooltip: {callbacks: {label: createTooltipCallback(yAxisLabel)}}
        }
      }
  );

  new Chart(ctx, config);
}

function createNetworkChart() {
  const ctx = document.getElementById('chart-network').getContext('2d');
  const bytesToGB = bytes => bytes / (1024 * 1024 * 1024);

  const networkInData = systemMetrics.map(metric => bytesToGB(metric.networkInBytes));
  const networkOutData = systemMetrics.map(metric => bytesToGB(metric.networkOutBytes));

  const filteredNetworkInData = filterData(networkInData);
  const filteredNetworkOutData = filterData(networkOutData);

  const config = createChartConfig('line',
      systemMetrics.map(metric => metric.timestamp),
      [
        {
          label: 'Network In',
          data: filteredNetworkInData,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
        },
        {
          label: 'Network Out',
          data: filteredNetworkOutData,
          borderColor: 'rgb(255, 99, 132)',
          tension: 0.1
        }
      ],
      {
        scales: {
          x: {
            type: 'time',
            time: {unit: 'day'},
            title: {display: true, text: 'Time'}
          },
          y: {
            beginAtZero: true,
            title: {display: true, text: 'Gigabytes (GB)'},
            ticks: {
              callback: function(value) {
                return value.toFixed(2) + ' GB';
              }
            }
          }
        },
        plugins: {
          tooltip: {
            callbacks: {
              label: function(context) {
                let label = context.dataset.label || '';
                if (label) {
                  label += ': ';
                }
                if (context.parsed.y !== null) {
                  label += context.parsed.y.toFixed(2) + ' GB';
                }
                return label;
              }
            }
          }
        }
      }
  );

  new Chart(ctx, config);
}

function createEndpointMetricsChart() {
  const ctx = document.getElementById('endpointMetricsChart').getContext('2d');
  const visitCountData = recentEndpointMetrics.map(item => item.totalPageView);
  const responseTimeData = recentEndpointMetrics.map(
      item => item.averageResponseTime);
  const rateLimitData = recentEndpointMetrics.map(
      item => item.ratelimitFailCount);

  const filteredVisitCountData = filterData(visitCountData);
  const filteredResponseTimeData = filterData(responseTimeData);
  const filteredRateLimitData = filterData(rateLimitData);

  const config = createChartConfig('line',
      recentEndpointMetrics.map(item => item.date),
      [
        {
          label: 'Visit Count',
          data: filteredVisitCountData,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1,
          yAxisID: 'y-axis-1'
        },
        {
          label: 'Average Response Time (ms)',
          data: filteredResponseTimeData,
          borderColor: 'rgb(255, 99, 132)',
          tension: 0.1,
          yAxisID: 'y-axis-2'
        },
        {
          label: 'Rate Limit Failures',
          data: filteredRateLimitData,
          borderColor: 'rgb(54, 162, 235)',
          tension: 0.1,
          yAxisID: 'y-axis-3'
        }
      ],
      {
        scales: {
          x: {title: {display: true, text: 'Date'}},
          'y-axis-1': {
            type: 'linear',
            display: true,
            position: 'left',
            title: {display: true, text: 'Visit Count'},
          },
          'y-axis-2': {
            type: 'linear',
            display: true,
            position: 'right',
            title: {display: true, text: 'Avg Response Time (ms)'},
            grid: {drawOnChartArea: false},
          },
          'y-axis-3': {
            type: 'linear',
            display: true,
            position: 'right',
            title: {display: true, text: 'Rate Limit Failures'},
            grid: {drawOnChartArea: false},
          },
        },
        plugins: {
          legend: {position: 'top'},
          title: {display: true, text: 'Endpoint Metrics'}
        }
      }
  );

  new Chart(ctx, config);
}

const noDataMessagePlugin = {
  id: 'noDataMessage',
  afterDraw: (chart, args, options) => {
    if (chart.data.datasets.every(dataset => dataset.data.length === 0)) {
      const {ctx, chartArea: {left, top, right, bottom}} = chart;
      const message = options.message || 'No data available';
      const fontSize = options.font?.size || 16;
      const fontFamily = options.font?.family || 'Arial';
      const color = options.color || 'rgba(100, 100, 100, 0.8)';

      ctx.save();
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.font = `${fontSize}px ${fontFamily}`;
      ctx.fillStyle = color;
      ctx.fillText(message, (left + right) / 2, (top + bottom) / 2);
      ctx.restore();
    }
  }
};

Chart.register(noDataMessagePlugin);
