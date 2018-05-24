import React from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title, Switch} from 'native-base';
import {StyleSheet, ScrollView, NativeModules} from 'react-native';
import Row from '../../components/Row';


class Home extends React.Component {

  constructor(props) {
    super(props);
    this.props.navigator.setTitle({title: 'mCerebrum'});
    this.props.navigator.setOnNavigatorEvent(this.onNavigatorEvent.bind(this));
    this.state = {
             switch1Value: false,
          }
  }
       toggleSwitch1 = (value) => {
          this.setState({switch1Value: value})
          console.log('Switch 1 is: ' + value)
       }
  onNavigatorEvent(event) {
    if (event.type === 'DeepLink') {
      const parts = event.link.split('/');
      if (parts[0] === 'tab1') {
        this.props.navigator.push({
          screen: parts[1]
        });
      }
    }
  }

  render() {
    return (

     <Container style={{backgroundColor: '#131325'}}>
        <Content>
          <Card style={{backgroundColor: '#1e2c3c'}}>
           <CardItem header  style={{ backgroundColor: '#00838F'}}>
           <Left/>
           <Body>
             <Text style={{color: 'white'}}>Data Colletion</Text>
             </Body>
             <Right/>
           </CardItem>
           <CardItem style={{ backgroundColor: '#1e2c3c'}}>

           <Left>
           <Text style={{color: 'white'}}>Data Collection OFF</Text>
           </Left>

 <Right>
<Switch onValueChange = {this.toggleSwitch1}
            value = {this.state.switch1Value} onTintColor='green' />

</Right>
           </CardItem>
                    </Card>

                    <Card style={{backgroundColor: '#1e2c3c'}}>
                     <CardItem header style={{ backgroundColor: '#00838F'
                      }}>

                     <Body>
                       <Text style={{ color: 'white', textAlign: 'center'}}>Pause and Resume Data Collection</Text>
                       </Body>

                     </CardItem>
                     <CardItem style={{ backgroundColor: '#1e2c3c'}}>

           <Body>
           <Text style={{ color: 'white'}}>Data Collection Active</Text>
           </Body>
           <Right>
           <Button transparent>
            <Icon active name="controller-paus" type="Entypo" style={{fontSize: 30, color: 'green'}}/>
          </Button>
                       </Right>
                     </CardItem>
                              </Card>
        </Content>
      </Container>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  row: {
    height: 48,
    paddingHorizontal: 16,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    borderBottomWidth: 1,
    borderBottomColor: 'rgba(0, 0, 0, 0.054)'
  },
  text: {
    fontSize: 16,
  },
});

export default Home;
