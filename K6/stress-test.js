import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 2000 },
        { duration: '30s', target: 2000 },

        { duration: '10s', target: 2500 },
        { duration: '30s', target: 2500 },

        { duration: '10s', target: 3000 },
        { duration: '30s', target: 3000 },

        { duration: '10s', target: 0 },
    ],
};

export default function () {
    const res = http.get('http://localhost:8081/api/categorias');

    check(res, {
        'status 200': (r) => r.status === 200,
        'sin errores HTTP': (r) => r.status < 400,
        'respuesta es lista': (r) => r.body.startsWith('['),
        'tiempo menor a 3000ms': (r) => r.timings.duration < 3000,
    });

    sleep(1);
}
