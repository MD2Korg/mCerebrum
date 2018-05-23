import React from 'react';
import { Container, Header, Content, Card, CardItem, Thumbnail, Text, Button, Icon, Left, Body, Right, Fab, Title} from 'native-base';
import {StyleSheet, ScrollView} from 'react-native';
import Row from '../../components/Row';


class Home extends React.Component {

  constructor(props) {
    super(props);
    this.props.navigator.setTitle({title: 'mCerebrum'});
    this.props.navigator.setOnNavigatorEvent(this.onNavigatorEvent.bind(this));
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

     <Container>
        <Header/>

        <Content>
          <Card>
           <CardItem header>
             <Text>Data Collection</Text>
           </CardItem>
           <CardItem>
             <Body>

                 <Left>
                <Icon ios='ios-menu' android="md-menu" style={{fontSize: 20, color: 'red'}}/>
                 </Left>

             </Body>
           </CardItem>
           <CardItem footer>
             <Text>GeekyAnts</Text>
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
    borderBottomColor: 'rgba(0, 0, 0, 0.054)',
  },
  text: {
    fontSize: 16,
  },
});

export default Home;
