import React, { Component, PropTypes } from 'react'
import { connect } from 'react-redux'

class App extends Component {

  static propTypes = {
    // PropTypes
  }

  render() {
    return (
      <div>
        This is App
        <div>{this.props.children}</div>
      </div>
    )
  }
}

export default connect(state => ({
  // mapStateToProps
}), {
  // mapDispatchToProps
})(App)
