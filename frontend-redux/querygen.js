const fs = require('fs')
const path = require('path')
const R = require('ramda')
const stripMargin = require('stripmargin').stripMargin

const SOURCE_DIR = path.join(__dirname, 'app', 'scripts', 'queries');
const TARGET_FILE = path.join(__dirname, 'app', 'scripts', 'queries.js');

const graphQLRegex = /^(.+)\.graphql$/
const isGraphQLFile = file => file.match(graphQLRegex)
const nameNoExt = file => graphQLRegex.exec(file)[1]

const generateQueries = () => {
  console.log(`Generate queries from GraphQL files in ${SOURCE_DIR}...`)

  const queries = R.reject(R.isNil)(
    fs.readdirSync(SOURCE_DIR).map(file => {
      if (isGraphQLFile(file)) {
        return {
          name: nameNoExt(file),
          content: fs.readFileSync(path.join(SOURCE_DIR, file)).toString()
        }
      }
    }))

  const generated = stripMargin(`\
    import gql from 'graphql-tag'
    ${
      queries.map(q => `export const ${q.name} = gql\`\n${
        q.content.split('\n').map(line => `|  ${line}`).join('\n')
      }\``).join('\n')
    }`)

  fs.writeFileSync(TARGET_FILE, generated)

  console.log(`Wrote file ${TARGET_FILE} with generated queries`)
}

generateQueries()
