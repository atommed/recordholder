import React from 'react'

class Track extends React.Component {
    constructor(){
        super();
        this.onPlayClick = this.onPlayClick.bind(this)
    }

    onPlayClick(){
        this.props.handlePlay(this.props.trackId)
    }

    render(){
        return (
            <div>
                {this.props.name}
                <button className="btn" onClick={this.onPlayClick}>Play</button>
            </div>
        )
    }
}
export default Track