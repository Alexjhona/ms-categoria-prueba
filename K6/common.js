import http from 'k6/http';
import { check, sleep } from 'k6';

const CATEGORIAS_URL = 'http://localhost:8081/api/categorias';

export function consultarCategorias(maxDurationMs, waitSeconds = 0) {
    const res = http.get(CATEGORIAS_URL);

    check(res, {
        'status 200': (r) => r.status === 200,
        'sin errores HTTP': (r) => r.status < 400,
        'respuesta es JSON': (r) => r.headers['Content-Type'] && r.headers['Content-Type'].includes('application/json'),
        'respuesta es lista': (r) => r.body.startsWith('['),
        [`tiempo menor a ${maxDurationMs}ms`]: (r) => r.timings.duration < maxDurationMs,
    });

    if (waitSeconds > 0) {
        sleep(waitSeconds);
    }
}
