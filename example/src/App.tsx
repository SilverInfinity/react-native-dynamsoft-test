// @ts-nocheck
import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity, Image } from 'react-native';
import DynasoftBridge from 'react-native-dynasoft';

const licenseKey = 'DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9';

export default function App() {
  const [result, setResult] = React.useState();

  const [licensed, setLicenced] = React.useState(false);
  console.log('render', result);
  React.useEffect(() => {
    DynasoftBridge.setLicenceKey(licenseKey)
      .then((res) => {
        console.log('it returned!', res);
        setLicenced(res);
      })
      .catch((e) => {
        console.log(e);
      });
  }, []);
  const handlePress = React.useCallback(() => {
    console.log('scan start');
    DynasoftBridge.scanWithConfiguration({}).then((result) => {
      console.log('scan then', result);
      setResult(result);
    });
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Text>Licensed: {licensed ? 'yes' : 'no'}</Text>
      <TouchableOpacity onPress={handlePress}>
        <View color="#32a852" style={styles.box}>
          <Text>Open Camera</Text>
        </View>
      </TouchableOpacity>
      {result && (
        <>
          <Text>Image Result:</Text>
          <Image
            source={{ uri: result }}
            style={styles.image}
            resizeMode="contain"
          />
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    padding: 20,
    marginVertical: 20,
    backgroundColor: '#abc123',
    alignItems: 'center',
    justifyContent: 'center',
  },
  image: {
    width: 400,
    height: 400,
  },
});
