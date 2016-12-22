import * as Queries from '../queries'
import { fetchGraphQL } from './graphql'

export const SelectBackend = {
  Action: 'SELECT_BACKEND'
}

export const selectBackend = backend => ({
  type: SelectBackend.Action,
  payload: backend
})
