import Immutable, { Map, List } from 'immutable'
import { SelectBackend } from '../actions/app'

const initialState = Map({
  backend: 'backend1'
})

export default (state = initialState, action) => {
  switch (action.type) {
    case SelectBackend.Action:
      return state.set('backend', action.payload)
  }

  return state
}
