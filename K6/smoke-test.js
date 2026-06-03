import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 20,
    duration: '20s',
};

export default function () {
    const res = http.get('http://localhost:8081/api/categorias');

    check(res, {
        'status 200': (r) => r.status === 200,
        'sin errores HTTP': (r) => r.status < 400,
        'respuesta es lista': (r) => r.body.startsWith('['),
        'tiempo menor a 1500ms': (r) => r.timings.duration < 1500,
    });

    sleep(1);
}