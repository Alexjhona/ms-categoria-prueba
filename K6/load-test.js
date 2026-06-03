import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1,
    duration: '5s',
};

export default function () {
    const res = http.get('http://localhost:8081/api/categorias');

    check(res, {
        'status es 200': (r) => r.status === 200,
        'respuesta es JSON': (r) => r.headers['Content-Type'] && r.headers['Content-Type'].includes('application/json'),
        'respuesta es lista': (r) => r.body.startsWith('['),
        'tiempo menor a 1000ms': (r) => r.timings.duration < 1000,
    });
}