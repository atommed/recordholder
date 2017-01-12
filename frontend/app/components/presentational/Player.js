import React from 'react'
import {PlayPauseButton,SkipNextButton, SkipPrevButton} from '../PlayerButtons'

class Player extends React.Component {
    constructor(){
        super();
        this.state = {progress: 0};
    }

    refAudio =(audio)=>{
        this.audio = audio;
        audio.addEventListener("timeupdate", ()=>{
            this.setState({
                progress: audio.currentTime / audio.duration
            });
        });
    };

    ensurePauseState(props){
        if(props.isPaused)
            this.audio.pause();
        else {
            this.audio.play();
        }
    }

    componentDidUpdate(){
        this.ensurePauseState(this.props);
    }


    componentDidMount(){
        this.ensurePauseState(this.props)
    }

    componentWillReceiveProps(newProps) {
        this.setState({progress: 0});
        this.ensurePauseState(newProps);
    }

    sliderClick = (e) =>{
        const progress = e.nativeEvent.offsetX / this.slider.offsetWidth;
        this.audio.currentTime = progress * this.audio.duration;
        this.setState({progress});
    };

    render() {
        return (
            <div className="player-panel">
                <SkipPrevButton/>
                <PlayPauseButton/>
                <SkipNextButton/>
                <img className="coverArt" src={this.props.imgSrc} />
                <audio ref={this.refAudio}
                       src={this.props.src}>
                    Ur browser doesn't support HTML5 <code>audio</code> element =\
                </audio>
                <div className="player-track-info">
                    <div>Title: {this.props.title}</div>
                    {this.props.albumName &&
                    <div>Album: {this.props.albumName} </div>}
                    {this.props.artistName &&
                    <div>Artist: {this.props.artistName}</div>}
                </div>
                <div className="player-slider">
                    <div className="player-slider-body" ref={(el)=>{this.slider=el;}} onClick={this.sliderClick}>
                        <div style={{width: this.state.progress * 100 + "%"}} className="player-slider-listened"/>
                    </div>
                </div>
            </div>
        )
    }
}

Player.propTypes = {
    src: React.PropTypes.string.isRequired,
    isPaused: React.PropTypes.bool.isRequired,
    title: React.PropTypes.string.isRequired,
    albumName: React.PropTypes.string,
    artistName: React.PropTypes.string,
    imgSrc: React.PropTypes.string
};

export default Player;