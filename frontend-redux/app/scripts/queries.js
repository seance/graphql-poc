import gql from 'graphql-tag'

export const characters = gql`
  query Characters($withAssociates: Boolean!) {
    characters {
      id
      name
      associates @include(if: $withAssociates) {
        relation
        character {
          id
          name
        }
      }
    }
  }`
