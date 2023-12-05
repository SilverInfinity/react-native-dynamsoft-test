// @ts-nocheck

// figure out how to log
import * as React from 'react';

import {
  StyleSheet,
  Platform,
  View,
  Text,
  TouchableOpacity,
  Image,
} from 'react-native';
import DynamsoftBridge from 'react-native-dynamsoft';
import { launchCamera } from 'react-native-image-picker';

const licenseKey = 'DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9';

export default function App() {
  const [result, setResult] = React.useState();

  const [licensed, setLicenced] = React.useState(false);
  console.log('render', result);
  React.useEffect(() => {
    DynamsoftBridge.setLicenceKey(licenseKey)
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
    if (Platform.OS === 'android') {
      DynamsoftBridge.scanWithConfiguration({}).then((result) => {
        console.log('scan then', result);
        setResult(result);
      });
    } else {
      // ios
      launchCamera({}).then(async (result) => {
        if (result.didCancel) {
          console.log('user canceled');
        } else if (result.errorCode) {
          console.log('error thrown:', { ...result });
        } else {
          const originalUri = result.assets[0]?.uri;
          try {
            const normalizedUri = await DynamsoftBridge.normalizeFromFile(
              originalUri.replace('file://', '/')
            );
            setResult('file://' + normalizedUri);
          } catch (error) {
            console.log('CAUGHT ERROR!', { error });
          }
        }
      });
    }
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
