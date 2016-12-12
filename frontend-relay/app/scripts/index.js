import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import es6promise from 'es6-promise/auto';
import fetch from 'isomorphic-fetch';
import gql from 'graphql-tag';
import { print } from 'graphql-tag/printer';

const queryCharacters = gql`
  query Characters($withAssociates: Boolean!) {
    characters {
      id
      name
      associates @include(if: $withAssociates) {
        relation,
        character {
          id
          name
        }
      }
    }
  }`;

const fetchGraphQL = (query, variables) => {
  const opName = query.definitions[0].name;
  return fetch('/backend1/graphql', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      query: print(query),
      operationName: opName ? opName.value : null,
      variables: variables
    })
  }).then(r => r.json());
};

class CharacterList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      characters: []
    };
  }
  componentDidMount() {
    fetchGraphQL(queryCharacters, {
      withAssociates: this.props.withAssociates
    }).then(json => {
      this.setState({
        characters: json.data.characters
      });
    });
  }
  render() {
    return (
      <div>
        <h2>Characters</h2>
        <ul>
          {this.state.characters.map(c =>
            <li key={`character-${c.id}`}>
              {c.name}
              <ul>
              {(c.associates || []).map(a =>
                <li key={`${c.id}-associate-${a.character.id}`}>
                  {a.character.name} ({a.relation})
                </li>
              )}
              </ul>
            </li>
          )}
        </ul>
      </div>
    );
  }
}

ReactDOM.render(
  <CharacterList withAssociates={true}/>,
  document.getElementById('root'));
