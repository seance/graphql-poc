import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'
import { push } from 'redux-router'

class App extends Component {

  static propTypes = {
    push: P.func.isRequired
  }

  render() {
    const { push, location } = this.props
    return (
      <div>
        This is App at {location.pathname}
        <nav>
          <span onClick={() => push('/')}>Home </span>
          <span onClick={() => push('/planets')}>Planets </span>
          <span onClick={() => push('/species')}>Species </span>
        </nav>
        <div>
          {this.props.children}
        </div>
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.router
}), {
  // mapDispatchToProps
  push
})(App)
