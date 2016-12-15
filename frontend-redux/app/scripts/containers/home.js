import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'

import { fetchCharacters } from '../actions/home'
import CharacterList from '../components/characterList'

class Home extends Component {

  static propTypes = {
    isFetching: PropTypes.bool.isRequired,
    characters: PropTypes.arrayOf(PropTypes.object).isRequired,
    withAssociates: PropTypes.bool.isRequired
  }

  componentDidMount() {
    const { fetchCharacters, withAssociates } = this.props
    fetchCharacters({ withAssociates })
  }

  render() {
    const { isFetching, characters, withAssociates } = this.props
    return (
      <div>
        This is Home
        {
          isFetching ? (
            <span>Loading...</span>
          ) : (
            <CharacterList
              characters={characters}
              withAssociates={withAssociates}/>
          )
        }
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.home.toJS()
}), {
  // mapDispatchToProps
  fetchCharacters
})(Home)
