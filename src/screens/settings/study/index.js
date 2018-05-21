import React, { Component } from 'react';
import { Container, Header, Title,Content, List, ListItem, Text, Icon, Left, Body, Right, Switch, Separator, Button, View } from 'native-base';
export default class ListIconExample extends Component {
  render() {
    return (
      <Container>
<Content>
<Separator bordered><Text>Active Studies</Text></Separator>
          <List>
            <ListItem icon><Body><Text>IARPA study</Text></Body><Right><Icon name="arrow-forward" /></Right></ListItem>
          </List>
          <View style={{flexDirection: "row"}}>
          <Button iconLeft>
                      <Icon name='qrcode' type="FontAwesome"/>
                      <Text>QR Code</Text>
                    </Button>
                    <Button iconLeft>
                                <Icon name='login' type="Entypo"/>
                                <Text>Login to CC</Text>
                              </Button>
</View>
                                 </Content>
      </Container>
    );
  }
}
