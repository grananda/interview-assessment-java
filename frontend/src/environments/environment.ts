/**
 * Runtime configuration.
 *
 * `useApiMocks`: when true, an HTTP interceptor answers /api/* requests with
 * in-memory data instead of hitting the backend. Keep it `true` for the
 * StackBlitz (frontend-only) demo; set it to `false` to talk to the real
 * Spring Boot backend through the dev-server proxy.
 */
export const environment = {
  useApiMocks: true,
};
