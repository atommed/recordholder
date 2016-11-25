import React, {PropTypes} from 'react'
import {PlayButton,SkipNextButton, SkipPrevButton} from '../play-buttons'

class Player extends React.Component {
    ensurePauseState(props){
        if(props.pause)
            this.audio.pause();
        else
            this.audio.play();
    }


    componentDidMount(){
        this.ensurePauseState(this.props)
    }

    componentWillReceiveProps(newProps) {
        this.ensurePauseState(newProps)
    }

    render() {
        return (
            <div className="player-panel" style={{height: "100%"}}>
                <img className="coverArt" src={this.props.coverSrc} />
                <SkipPrevButton/>
                <PlayButton />
                <SkipNextButton/>
                <audio ref={(audio)=> {this.audio = audio;}}
                       src={this.props.src}>
                    Ur browser doesn't support HTML5 <code>audio</code> element =\
                </audio>
            </div>
        )
    }
}

Player.propTypes = {
    src: PropTypes.string,
    pause: PropTypes.bool,
    imgSrc: PropTypes.string.isRequired
};

export default Player;