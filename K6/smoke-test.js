import { consultarCategorias } from './common.js';

export const options = {
    vus: 1,
    duration: '5s',
};

export default function () {
    consultarCategorias(1000);
}
