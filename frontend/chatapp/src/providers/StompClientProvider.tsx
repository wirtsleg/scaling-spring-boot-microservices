import {createContext, FC, useContext, useState} from 'react';
import {Client} from '@stomp/stompjs';

import {useMount} from 'lib';

const StompClientContext = createContext<Client | null>(null);

export const StompClientProvider: FC = ({ children }) => {
  const [client, setClient] = useState<Client | null>(null);
  const [isReady, setReady] = useState(false);

  useMount(() => {
    const client = new Client({
      brokerURL: buildWsUrl(),
      reconnectDelay: 1000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => setReady(true),
      onDisconnect: () => setReady(false),
    });

    client.activate();
    setClient(client);
  });

  if (!isReady) return <div>STOMP client is not ready...</div>;

  return (
    <StompClientContext.Provider value={client}>
      {children}
    </StompClientContext.Provider>
  );
};

export const useStompClientContext = () =>
  useContext(StompClientContext) as Client;

function buildWsUrl() {
  const url = new URL('/chat', window.location.href);

  url.protocol = url.protocol.replace('http', 'ws');

  return url.href;
}
