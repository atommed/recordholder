import React from 'react'
import Track from  '../Track'

class AlbumView extends React.Component{
    constructor(){
        super();
        this.loadData = this.loadData.bind(this);
        this.listTracks = this.listTracks.bind(this);
        this.state = {
            tracks: []
        }
    }

    loadData(props){
        $.ajax({
            url: `/api/albums/${this.props.params.albumId}`
        }).done(data=>{
            this.setState({tracks: data});
            data.map(this.props.saveTrack)
        })
    }

    componentWillMount(){
        this.loadData(this.props)
    }

    componentWillReceiveProps(newProps){
        if(newProps.params.albumId !== this.props.params.albumId){
            this.loadData(newProps)
        }
    }

    renderTrack(track){
        return (
            <Track key={track.id} trackId={track.id} name={track.title}/>
        )
    }

    listTracks(){
        return this.state.tracks.map(this.renderTrack)
    }

    render(){
        return (
            <div>
                {this.listTracks()}
            </div>
        )
    }
}

export default AlbumView