import React, {PropTypes} from 'react'

class Player extends React.Component {
    componentWillReceiveProps(newProps) {
        if (newProps.pause)
            this.audio.pause();
        else
            this.audio.play();
    }

    render() {
        return (
            <div>
                <audio ref={(audio)=> {this.audio = audio;}}
                       src={this.props.src}>
                    Ur browser doesn't support HTML5 <code>audio</code> element =\
                </audio>
            </div>
        )
    }
}

Player.propTypes = {
    src: PropTypes.string.isRequired,
    pause: PropTypes.bool
}

export default Player;