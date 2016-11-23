import React from 'react'


class UploadForm extends React.Component{
    constructor(props){
        super(props);
        this.state = {uploadResult: ""};
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(ev){
        ev.preventDefault();
        var oData = new FormData(this.form);
        var req = new XMLHttpRequest();
        req.open("POST","api/tracks/upload", true);
        req.onload = ()=>{
            if(req.status==200)
                this.setState({uploadResult: req.response});
            else
                this.setState({uploadResult: "Error " + req.status+ ":"+req.response});
        };
        req.send(oData);
    }

    render(){
        return (
            <div>
                {this.state.uploadResult}
                <form ref={(form)=>{this.form = form;}}
                      onSubmit={this.handleSubmit}
                      encType="multipart/form-data"
                      method="post">
                    <input type="file" required name="track"/>
                    <input type="submit" value="Upload file!"/>
                </form>
            </div>
        )
    }
}

export {UploadForm};