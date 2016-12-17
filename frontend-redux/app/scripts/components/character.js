import React, { Component, PropTypes as P } from 'react'
import R from 'ramda'

export default class Character extends Component {

  static propTypes = {
    character     : P.object.isRequired,
    clickCharacter: P.func.isRequired,
    clickPlanet   : P.func.isRequired,
    clickSpecies  : P.func.isRequired,
  }

  render() {
    const { character: c } = this.props
    return c.kind === 'organic'
      ? this.renderOrganic(c)
      : this.renderDroid(c)
  }

  renderOrganic(c) {
    const {
      clickCharacter,
      clickPlanet,
      clickSpecies
    } = this.props

    return (
      <div className="character character-organic">
        <div className="species-panel" onClick={clickSpecies(c.species.id)}>
          <div className="species-icon"></div>
          <div className="species-name">
            {c.species.name}
          </div>
        </div>
        <div className="info-panel">
          <div className="name" onClick={clickCharacter(c.id)}>{c.name}</div>
          <div className="weapon-panel">
            <div className="weapon-icon">Favorite weapon</div>
            <div className="favorite-weapon">
              {R.pathOr('None', ['favoriteWeapon', 'name'], c)}
            </div>
          </div>
          <div className="planet-panel" onClick={clickPlanet(c.homePlanet.id)}>
            <div className="planet-icon">Home planet</div>
            <div className="planet-name">{c.homePlanet.name}</div>
          </div>
        </div>
        <div className="faction-panel">
          <div className={this.factionIconClass(c)}></div>
          <div className="faction-name">{this.factionName(c)}</div>
        </div>
      </div>
    )
  }

  renderDroid(c) {
    const {
      clickCharacter,
      clickPlanet,
      clickSpecies
    } = this.props

    return (
      <div className="character character-droid">
        <div className="droid-panel">
          <div className="droid-icon"></div>
          <div className="droid-label">Droid</div>
        </div>
        <div className="info-panel">
          <div className="name" onClick={clickCharacter(c.id)}>{c.name}</div>
          <div className="weapon-panel">
            <div className="weapon-icon">Favorite weapon</div>
            <div className="favorite-weapon">
              {R.pathOr('None', ['favoriteWeapon', 'name'], c)}
            </div>
          </div>
          <div className="function-panel">
            <div className="function-icon">Primary function</div>
            <div className="function-name">{c.primaryFunction}</div>
          </div>
        </div>
        <div className="faction-panel">
          <div className={this.factionIconClass(c)}></div>
          <div className="faction-name">{this.factionName(c)}</div>
        </div>
      </div>
    )
  }

  factionIconClass(c) {
    switch (c.faction) {
      case 'RebelAlliance':
        return 'faction-icon-rebel'
      case 'GalacticEmpire':
        return 'faction-icon-empire'
      default:
        return 'faction-icon-unknown'
    }
  }

  factionName(c) {
    switch (c.faction) {
      case 'RebelAlliance':
        return 'Rebel Alliance'
      case 'GalacticEmpire':
        return 'Galactic Empire'
      default:
        return 'Unknown'
    }
  }
}
