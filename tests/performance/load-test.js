// =====================================================================
// load-test.js - Script de Prueba de Carga con k6
// Proyecto Integrador DevOps + IaC · Evaluación del Rendimiento (7%)
// =====================================================================
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '10s', target: 5 },   // Ramp-up: 5 usuarios virtuales
    { duration: '30s', target: 20 },  // Carga sostenida: 20 usuarios virtuales
    { duration: '10s', target: 0 },   // Ramp-down: enfriamiento a 0
  ],
  thresholds: {
    // Latencia p95 menor a 500ms
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    // Tasa de error menor al 1%
    'errors': ['rate<0.01'],
  },
};

const BASE_URL_BACKEND = __ENV.BACKEND_URL || 'http://localhost:8081';
const BASE_URL_FRONTEND = __ENV.FRONTEND_URL || 'http://localhost:8080';

export default function () {
  // 1. Healthcheck Backend
  let resHealth = http.get(`${BASE_URL_BACKEND}/actuator/health`);
  let successHealth = check(resHealth, {
    'Backend Health status is 200': (r) => r.status === 200,
  });
  errorRate.add(!successHealth);

  // 2. Swagger UI
  let resSwagger = http.get(`${BASE_URL_BACKEND}/swagger-ui.html`);
  let successSwagger = check(resSwagger, {
    'Swagger UI status is 200': (r) => r.status === 200 || r.status === 302,
  });
  errorRate.add(!successSwagger);

  // 3. API Usuarios
  let resUsuarios = http.get(`${BASE_URL_BACKEND}/usuario/all`);
  let successUsuarios = check(resUsuarios, {
    'API Usuarios status is 200': (r) => r.status === 200,
  });
  errorRate.add(!successUsuarios);

  // 4. API Proyectos
  let resProyectos = http.get(`${BASE_URL_BACKEND}/proyectos/all`);
  let successProyectos = check(resProyectos, {
    'API Proyectos status is 200': (r) => r.status === 200,
  });
  errorRate.add(!successProyectos);

  // 5. Frontend Web
  let resFrontend = http.get(`${BASE_URL_FRONTEND}/`);
  let successFrontend = check(resFrontend, {
    'Frontend Web status is 200': (r) => r.status === 200,
  });
  errorRate.add(!successFrontend);

  sleep(1);
}
