import React, {PropTypes} from 'react'

function PlayerButton(props){
    return(
        <button onClick={props.handleClick}
            className="player-btn">
            {props.children}
        </button>
    )
}

function PlayButton(props) {
    return (
        <PlayerButton handleClick={props.handleClick}>
            <i className="material-icons">{props.isPaused? "play_arrow" : "pause"}</i>
        </PlayerButton>
    )
}

function SkipNextButton(props){
    return (
        <PlayerButton handleClick={props.handleClick}>
            <i className="material-icons">skip_next</i>
        </PlayerButton>
    )
}

function SkipPrevButton(props){
    return (
        <PlayerButton handleClick={props.handleClick}>
            <i className="material-icons">skip_previous</i>
        </PlayerButton>
    )
}



PlayerButton.propTypes = {
    handleClick: PropTypes.func.isRequired
};

PlayButton.propTypes = {
    isPaused: PropTypes.bool.isRequired
};

export {PlayButton, SkipNextButton, SkipPrevButton}