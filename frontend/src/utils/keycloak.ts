import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:7080',
  realm: 'TwotterFrontend',
  clientId: 'twotter',
});

export default keycloak;
