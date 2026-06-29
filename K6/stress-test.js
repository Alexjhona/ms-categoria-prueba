import { consultarCategorias } from './common.js';

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
    consultarCategorias(3000, 1);
}
