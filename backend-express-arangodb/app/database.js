import * as habitat from 'habitat';
import { Database } from 'arangojs';

const db = habitat.get('DB');
const config = {
  url: `http://${db.user}:${db.password}@${db.host}:${db.port}`,
  databaseName: db.name
};

// console.log(`Connecting to DB using config: ${JSON.stringify(config, null, 2)}`);

export default new Database(config);
