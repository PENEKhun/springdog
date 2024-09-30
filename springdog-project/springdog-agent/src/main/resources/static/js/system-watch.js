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
let systemMetricsGlobal = []; // To keep a global reference of systemMetrics
let memos = [];

function initializeCharts(systemMetrics) {
  systemMetricsGlobal = systemMetrics; // Store globally for access in saveMemo
  systemMetrics.forEach(metric => {
    if (metric.memo && metric.memo.trim().length > 0) {
      memos.push({
        id: metric.id,
        timestamp: new Date(metric.timestamp).getTime(),
        description: metric.memo,
        label: metric.label || "",
        chart: 'both'
      });
    }
  });

  resourceChart = createResourceUsageChart(systemMetrics);
  networkChart = createNetworkTrafficChart(systemMetrics);

  renderMemoList();
}

function createResourceUsageChart(systemMetrics) {
  const ctx = document.getElementById('resource-usage-chart').getContext('2d');

  const labels = systemMetrics.map(metric => new Date(metric.timestamp));

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

  const annotations = createAnnotations('resource', systemMetrics);
  console.log(annotations);

  return new Chart(ctx, {
    type: 'line',
    data: {labels, datasets},
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: 'System Resource Usage Over Time',
          font: {size: 18}
        },
        tooltip: {
          callbacks: {
            afterBody: (context) => {
              const label = context[0].label;
              const memoForThisPoint = memos.find(memo =>
                  memo.timestamp === new Date(label).getTime() &&
                  (memo.chart === 'resource' || memo.chart === 'both')
              );
              return memoForThisPoint ? `Memo: ${memoForThisPoint.description}`
                  : '';
            }
          }
        },
        annotation: {
          annotations: annotations
        }
      },
      scales: {
        x: {
          type: 'time',
          time: {
            unit: 'minute',
            displayFormats: {
              minute: 'MMM d, h:mm a'
            }
          },
          title: {
            display: true,
            text: 'Time'
          }
        },
        y: {
          title: {display: true, text: 'Usage (%)', font: {size: 14}},
          min: Math.max(0, minValue - 5),
          max: Math.min(100, maxValue + 5),
          ticks: {stepSize: 5, font: {size: 12}}
        },
      },
      onClick: (event, elements) => {
        if (elements.length > 0) {
          const index = elements[0].index;
          const metric = systemMetrics[index];
          openMemoModal('resource', metric.id, metric.timestamp);
        }
      }
    }
  });
}

function createNetworkTrafficChart(systemMetrics) {
  const ctx = document.getElementById('network-traffic-chart').getContext('2d');

  const labels = systemMetrics.map(metric => new Date(metric.timestamp));

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

  const annotations = createAnnotations('network', systemMetrics);

  return new Chart(ctx, {
    type: 'line',
    data: {labels, datasets},
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: 'Network Traffic Over Time',
          font: {size: 18}
        },
        tooltip: {
          callbacks: {
            afterBody: (context) => {
              const label = context[0].label;
              const memoForThisPoint = memos.find(memo =>
                  memo.timestamp === new Date(label).getTime() &&
                  (memo.chart === 'network' || memo.chart === 'both')
              );
              return memoForThisPoint ? `Memo: ${memoForThisPoint.description}`
                  : '';
            }
          }
        },
        annotation: {
          annotations: annotations
        }
      },
      scales: {
        x: {
          type: 'time',
          time: {
            unit: 'minute',
            displayFormats: {
              minute: 'MMM d, h:mm a'
            }
          },
          title: {
            display: true,
            text: 'Time'
          }
        },
        y: {
          title: {display: true, text: 'Traffic (MB)', font: {size: 14}},
          min: 0,
          max: maxValue * 1.1,
          ticks: {
            stepSize: maxValue > 5 ? Math.ceil(maxValue / 5) : 1,
            font: {size: 12}
          }
        }
      },
      onClick: (event, elements) => {
        if (elements.length > 0) {
          const index = elements[0].index;
          const metric = systemMetrics[index];
          openMemoModal('network', metric.id, metric.timestamp);
        }
      }
    }
  });
}

function getYValueForTimestamp(systemMetrics, timestamp, metricKey) {
  const metric = systemMetrics.find(
      m => new Date(m.timestamp).getTime() === timestamp);
  return metric ? metric[metricKey] : 0;
}

function openMemoModal(chartType, metricId, timestamp) {
  const modal = new bootstrap.Modal(document.getElementById('memoModal'));
  const timestampLabel = document.getElementById('timestamp');
  timestampLabel.textContent = new Date(timestamp).toLocaleString();
  const form = document.getElementById('memoForm');
  form.reset();

  const existingMemo = memos.find(memo =>
      memo.timestamp === new Date(timestamp).getTime() &&
      (memo.chart === chartType || memo.chart === 'both')
  );
  if (existingMemo) {
    document.getElementById('memoDescription').value = existingMemo.description;
    $('#memoLabel').val(existingMemo.label).trigger('change');
  }

  document.getElementById('saveMemoBtn').onclick = () => saveMemo(chartType,
      metricId,
      timestamp);

  modal.show();
}

function saveMemo(chartType, metricId, timestamp) {
  const description = document.getElementById('memoDescription').value.trim();
  const label = $('#memoLabel').val(); // Assuming select2 is used

  if (!description) {
    alert('Description cannot be empty.');
    return;
  }

  const memoTimestamp = new Date(timestamp).getTime();

  // Prepare memo object
  const newMemo = {
    chart: chartType,
    timestamp: memoTimestamp,
    description: description,
    label: label
  };

  // Send POST request to add memo
  springdogFetch(`/system-watch/${metricId}/memo?description=${description}`,
      {method: 'POST'})
  .then(responseMessage => {
    // Update memos array
    let existingMemo = memos.find(memo =>
        memo.timestamp === memoTimestamp &&
        (memo.chart === chartType || memo.chart === 'both')
    );
    if (existingMemo) {
      existingMemo.description = description;
      existingMemo.label = label;
    } else {
      memos.push(newMemo);
    }

    // Update annotations for both charts
    if (resourceChart) {
      resourceChart.options.plugins.annotation.annotations = createAnnotations(
          'resource', systemMetricsGlobal);
      resourceChart.update();
    }
    if (networkChart) {
      networkChart.options.plugins.annotation.annotations = createAnnotations(
          'network', systemMetricsGlobal);
      networkChart.update();
    }

    // Update memo list
    renderMemoList();

    // Close the modal
    const modalElement = document.getElementById('memoModal');
    const modalInstance = bootstrap.Modal.getInstance(modalElement);
    if (modalInstance) {
      modalInstance.hide();
    }
  })
  .catch(error => {
    alert(`Failed to add memo: ${error.message}`);
  });
}

function createAnnotations(chartType, systemMetrics) {
  const annotations = {};
  memos
  .filter(memo => memo.chart === chartType || memo.chart === 'both')
  .forEach((memo, index) => {
    annotations[`line${index}`] = {
      type: 'line',
      xMin: new Date(memo.timestamp),
      xMax: new Date(memo.timestamp),
      borderColor: chartType === 'resource' ? 'rgba(255, 0, 0, 0.7)' : 'rgba(0, 0, 255, 0.7)',
      borderWidth: 2,
      label: {
        content: memo.description,
        enabled: true,
        position: 'start',
        backgroundColor: 'rgba(0, 0, 0, 0.7)',
        color: '#fff',
        font: { size: 12 },
        padding: 6,
        cornerRadius: 4,
        yAdjust: -20
      }
    };
  });
  return annotations;
}

function renderMemoList() {
  const memoList = document.getElementById('memoList');
  memoList.innerHTML = '';
  memos.forEach(memo => {
    const listItem = document.createElement('li');
    listItem.className = 'list-group-item';
    listItem.textContent = `${new Date(
        memo.timestamp).toLocaleString()} - ${memo.description}`;
    memoList.appendChild(listItem);
  });
}
