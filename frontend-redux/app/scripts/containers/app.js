import React, { Component, PropTypes as P } from 'react'
import { connect } from 'react-redux'
import { push } from 'redux-router'

class App extends Component {

  static propTypes = {
    push: P.func.isRequired
  }

  render() {
    const { location } = this.props
    const active = {
      home: location.pathname === '/' ? 'is-active' : '',
      planets: location.pathname === '/planets' ? 'is-active' : '',
      species: location.pathname === '/species' ? 'is-active' : ''
    }
    return (
      <div>
        <header className="header">
          <h1>Star Wars</h1>
          <nav>
            <ul>
              <li className={active.home}><span onClick={this.clickNav('/')}>Home</span></li>
              <li className={active.planets}><span onClick={this.clickNav('/planets')}>Planets</span></li>
              <li className={active.species}><span onClick={this.clickNav('/species')}>Species</span></li>
            </ul>
          </nav>
        </header>
        <div>
          {this.props.children}
        </div>
      </div>
    )
  }

  clickNav(page) {
    return e => {
      e.preventDefault()
      this.props.push(page)
    }
  }
}

export default connect(state => ({
  // mapStateToProps
  ...state.router
}), {
  // mapDispatchToProps
  push
})(App)
