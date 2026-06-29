import { consultarCategorias } from './common.js';

export const options = {
    vus: 20,
    duration: '20s',
};

export default function () {
    consultarCategorias(1500, 1);
}
