import * as http from 'http';

const HOST = '127.0.0.1';
const PORT = 8080;
const JAVA_PORT = 8081;
const JAVA_BACKEND_URL = `http://${HOST}:${JAVA_PORT}`;

const server = http.createServer(async (request, response) => {
    const requestUrl = request.url || '';
    if (request.method !== 'GET' || !requestUrl.startsWith('/geocode')) {
        sendResponse(response, 404, 'Not Found');
        return;
    }
    try {
        const javaUrl = JAVA_BACKEND_URL + requestUrl;
        const javaResponse = await fetch(javaUrl);
        const data = await javaResponse.text();
        sendResponse(response, javaResponse.status, data);
    } catch (error) {
        console.error('Java backend unavailable:', error);
        sendResponse(response, 503, 'Error: Java backend is offline or unreachable.');
    }
});

server.listen(PORT, HOST, () => {
    console.log(`TypeScript API Server running on http://localhost:${PORT}`);
    console.log(`Forwarding requests to Java core at ${JAVA_BACKEND_URL}`);
});

function sendResponse(response: http.ServerResponse, statusCode: number, data: string) {
    response.writeHead(statusCode, {'Content-Type': 'text/plain; charset=utf-8'});
    response.end(data);
}