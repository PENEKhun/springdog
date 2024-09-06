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

let resourceChart, networkChart;
let memos = [];

function initializeCharts(systemMetrics) {
  resourceChart = createResourceUsageChart(systemMetrics);
  networkChart = createNetworkTrafficChart(systemMetrics);
}

function createResourceUsageChart(systemMetrics) {
  const ctx = document.getElementById('resource-usage-chart').getContext('2d');

  const labels = systemMetrics.map(metric => new Date(metric.timestamp).toLocaleString());

  const datasets = [
    {
      label: 'CPU Usage (%)',
      data: systemMetrics.map(metric => metric.cpuUsagePercent),
      borderColor: 'rgba(255, 99, 132, 1)',
      backgroundColor: 'rgba(255, 99, 132, 0.2)',
    },
    {
      label: 'Memory Usage (%)',
      data: systemMetrics.map(metric => metric.memoryUsagePercent),
      borderColor: 'rgba(54, 162, 235, 1)',
      backgroundColor: 'rgba(54, 162, 235, 0.2)',
    },
    {
      label: 'Disk Usage (%)',
      data: systemMetrics.map(metric => metric.diskUsagePercent),
      borderColor: 'rgba(255, 206, 86, 1)',
      backgroundColor: 'rgba(255, 206, 86, 0.2)',
    },
    {
      label: 'JVM Heap Usage (%)',
      data: systemMetrics.map(metric => metric.jvmHeapUsagePercent),
      borderColor: 'rgba(75, 192, 192, 1)',
      backgroundColor: 'rgba(75, 192, 192, 0.2)',
    },
    {
      label: 'JVM Non-Heap Usage (%)',
      data: systemMetrics.map(metric => metric.jvmNonHeapUsagePercent),
      borderColor: 'rgba(153, 102, 255, 1)',
      backgroundColor: 'rgba(153, 102, 255, 0.2)',
    }
  ];

  const allData = datasets.flatMap(dataset => dataset.data);
  const minValue = Math.min(...allData);
  const maxValue = Math.max(...allData);

  return new Chart(ctx, {
    type: 'line',
    data: { labels, datasets },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: 'System Resource Usage Over Time'
        },
        tooltip: {
          callbacks: {
            afterBody: (context) => {
              const memoForThisPoint = memos.find(memo =>
                  memo.chart === 'resource' && memo.timestamp === context[0].label
              );
              return memoForThisPoint ? `Memo: ${memoForThisPoint.description}` : '';
            }
          }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Time'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Usage (%)'
          },
          min: Math.max(0, minValue - 5),
          max: Math.min(100, maxValue + 5),
          ticks: {
            stepSize: 5
          }
        }
      },
      onClick: (event, elements) => {
        if (elements.length > 0) {
          const index = elements[0].index;
          openMemoModal('resource', labels[index]);
        }
      }
    },
  });
}

function createNetworkTrafficChart(systemMetrics) {
  const ctx = document.getElementById('network-traffic-chart').getContext('2d');

  const labels = systemMetrics.map(metric => new Date(metric.timestamp).toLocaleString());

  const datasets = [
    {
      label: 'Network In (MB)',
      data: systemMetrics.map(metric => metric.networkInBytes / (1024 * 1024)),
      borderColor: 'rgba(255, 159, 64, 1)',
      backgroundColor: 'rgba(255, 159, 64, 0.2)',
    },
    {
      label: 'Network Out (MB)',
      data: systemMetrics.map(metric => metric.networkOutBytes / (1024 * 1024)),
      borderColor: 'rgba(0, 128, 0, 1)',
      backgroundColor: 'rgba(0, 128, 0, 0.2)',
    }
  ];

  const allData = datasets.flatMap(dataset => dataset.data);
  const maxValue = Math.max(...allData);

  return new Chart(ctx, {
    type: 'line',
    data: { labels, datasets },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: 'Network Traffic Over Time'
        },
        tooltip: {
          callbacks: {
            afterBody: (context) => {
              const memoForThisPoint = memos.find(memo =>
                  memo.chart === 'resource' && memo.timestamp === context[0].label
              );
              return memoForThisPoint ? `Memo: ${memoForThisPoint.description}` : '';
            }
          }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Time'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Traffic (MB)'
          },
          min: 0,
          max: maxValue * 1.1,
          ticks: {
            stepSize: maxValue > 5 ? Math.ceil(maxValue / 5) : 1
          }
        }
      },
      onClick: (event, elements) => {
        if (elements.length > 0) {
          const index = elements[0].index;
          openMemoModal('network', labels[index]);
        }
      }
    },
  });
}

function openMemoModal(chartType, timestamp) {
  const modal = new bootstrap.Modal(document.getElementById('memoModal'));
  const timetampLabel = document.getElementById('timestamp');
  timetampLabel.textContent = timestamp;
  const form = document.getElementById('memoForm');
  form.reset();

  document.getElementById('saveMemoBtn').onclick = () => saveMemo(chartType, timestamp);

  modal.show();
}

function saveMemo(chartType, timestamp) {
  const description = document.getElementById('memoDescription').value;
  const label = document.getElementById('memoLabel').value;

  memos.push({ chart: chartType, timestamp, description, label });

  if (chartType === 'resource' && resourceChart) {
    resourceChart.update();
  } else if (chartType === 'network' && networkChart) {
    networkChart.update();
  }

  const modalElement = document.getElementById('memoModal');
  const modalInstance = bootstrap.Modal.getInstance(modalElement);
  if (modalInstance) {
    modalInstance.hide();
  }
}
