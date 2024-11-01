let selectedY = null;

function selectY(value, button) {
    selectedY = parseFloat(value);
    const yButtons = document.getElementsByName('Y-button');
    yButtons.forEach(btn => btn.classList.remove('active'));
    button.classList.add('active');
}

function validateValues() {
    const xValue = parseFloat(document.getElementById('X-input').value);
    const rValue = parseFloat(document.getElementById('R-input').value);

    const xlen = document.getElementById('X-input').value.split('.')[1];
    const rlen = document.getElementById('R-input').value.split('.')[1];


    if ((xlen && xlen.length > 15) || (rlen && rlen.length > 15)) {
        alert("Слишком большое количество знаков после запятой");
        throw new InvalidValueException("Слишком большое количество знаков после запятой");
    }

    if (isNaN(xValue) || xValue < -3 || xValue >= 5) {
        alert("Ошибка: значение X должно быть в диапазоне от -3 до 5.");
        throw new InvalidValueException('Ошибка: значение X должно быть в диапазоне от -3 до 5.');
    }

    if (selectedY === null) {
        alert("Ошибка: выберите значение Y.");
        throw new InvalidValueException('Ошибка: выберите значение Y.');
    }

    if (isNaN(rValue) || rValue < 2 || rValue >= 5) {
        alert("Ошибка: значение R должно быть в диапазоне от 2 до 5.");
        throw new InvalidValueException('Ошибка: значение R должно быть в диапазоне от 2 до 5.');
    }
    return true;
}

document.addEventListener("DOMContentLoaded", () => {
    function loadPreviousRequests() {
        const previousRequests = JSON.parse(localStorage.getItem('prev-requests')) || [];
        previousRequests.reverse().forEach(request => {
            printRow(request);
        });
    }

    loadPreviousRequests();

    document.getElementById('process-data').addEventListener('click', function (e) {
        e.preventDefault();
        
        validateValues();

        const xVal = parseFloat(document.querySelector('#X-input').value);
        const yVal = parseFloat(selectedY);
        const rVal = parseFloat(document.querySelector('#R-input').value);

        const body = {
            x: xVal,
            y: yVal,
            r: rVal
        };

        fetch(`/fcgi-bin/Web1.jar?x=${xVal}&y=${yVal}&r=${rVal}`, {
            method: 'GET',
        }).then(response => {
            console.log(`x=${xVal}, y=${yVal}, r=${rVal}`);
            if (!response.ok) {
                throw new Error(`${response.status}`);
            }
            return response.json();
        }).then((response) => {
            console.log(response);
            if (response.error) {
                alert(response.error);
            } else {
                printRow(response);
                const previousRequests = JSON.parse(localStorage.getItem('prev-requests')) || [];
                previousRequests.push(response);
                localStorage.setItem('prev-requests', JSON.stringify(previousRequests));
            }
        }).catch((error) => {
            alert(`There was an error processing your request: ${error.message}`);
        });
    });

    function printRow(data) {
        const newRow = document.createElement('tr');

        const xCell = document.createElement('td');
        xCell.textContent = data.x;
        newRow.appendChild(xCell);

        const yCell = document.createElement('td');
        yCell.textContent = data.y;
        newRow.appendChild(yCell);

        const rCell = document.createElement('td');
        rCell.textContent = data.r;
        newRow.appendChild(rCell);

        const resultCell = document.createElement('td');
        if(data.result == 'True') resultCell.textContent = "Point is inside the graph"
        else resultCell.textContent = "Point is outside the graph"

        newRow.appendChild(resultCell);

        const timeCell = document.createElement('td');
        const requestTime = new Date(data.current_time);
        timeCell.textContent = requestTime.toLocaleString('en-GB', {
            year: 'numeric',
            month: 'short',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
        newRow.appendChild(timeCell);

        const execTimeCell = document.createElement('td');
        execTimeCell.textContent = data.execution_time;
        newRow.appendChild(execTimeCell);

        const tableBody = document.querySelector('.graph-table table tbody');
        tableBody.insertBefore(newRow, tableBody.firstChild);
    }
});
