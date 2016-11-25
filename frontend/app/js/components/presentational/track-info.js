import React, {PropTypes} from 'react'

class TrackInfo extends React.Component{
    makeTr(k,v){
        return (
            <tr key={k}>
                <td>{k}</td>
                <td>{v}</td>
            </tr>
        )
    }

    tableData(){
        const res = [];
        const tags = this.props.tags;
        for(var key in tags){
            if(tags.hasOwnProperty(key)){
                res.push(this.makeTr(key,tags[key]))
            }
        }
        return res;
    }

    render(){
        return (
            <table className="bordered centered">
                <tbody>
                {this.tableData()}
                </tbody>
            </table>
        )
    }
}

export default TrackInfo;