# TPC protocol
### Request header format
| Bytes 0-1 | Byte 2    | Byte 3    | Byte 4    | Bytes 5-(N-1) | Byte N |
|-----------|-----------|-----------|-----------|---------------|--------|
| MSG_ID    | SEPARATOR | OPERATION | SEPARATOR | PAYLOAD       | END    |

#### Fields descriptions
- MSG ID: Message id is a counter of number of messages sent by the client,
  for any message the message counter have to be increased one by one, from 0 to 65535.
  If the max value is reached the next message will take a 0 value.
  The response from server to a request frame, use the same MSG_ID in the response frame.
- SEPARATOR: Is the semicolon character `;` or `0x3B` is ASCII.
- Operation: Define the type of frame sent from the client
    - `0x01` : Client hello
    - `0x02` : Client operation
    - `0x03` : Client bye
- PAYLOAD: Payload data on raw ASCII format.
- END: The character `$` or `0x24` point the frame end.

### Response header format
| Bytes 0-1 | Byte 2    | Bytes 3-(N-1) | Byte N |
|-----------|-----------|---------------|--------|
| MSG ID    | SEPARATOR | PAYLOAD       | END    |

#### Fields descriptions
- MSG ID: Value of a MSG IF from request message.
- SEPARATOR: Is the semicolon character `;` or `0x3B` is ASCII.
- PAYLOAD: Response value of a request message.
- END: The character `$` or `0x24` point the frame end.

### Operation Payload format
The payload for operations is a nibble array.

Accepted characteres:

| Character | Hex value |
|-----------|-----------|
| 0         | 0x0       |
| 1         | 0x1       |
| 2         | 0x2       |
| 3         | 0x3       |
| 4         | 0x4       |
| 5         | 0x5       |
| 6         | 0x6       |
| 7         | 0x7       |
| 8         | 0x8       |
| 9         | 0x9       |
| +         | 0xA       |
| -         | 0xB       |
| *         | 0xC       |
| /         | 0xD       |
| espacio   | 0xE       |
| ERROR     | 0xE       |
| FAIL      | 0xF       |

### Frames
#### Hello
The hello frame haven't payload. The server will be to respond with ACK frame if was success.

- Example REQ frame: `0x0000 0x3B 0x00 0x3B 0x24`
- Example RES frame: `0x0000 0x3B 0x06 0x24`

#### Operation
This frame ist sent for calculate operation in Reverse Polish Notation.
The operation its set in the frame payload and each operand have to be separated by space character.

If the operation is successful the server respond in response payload the result for the operation.

If the operation is failed for any reason the server return a FAIL payload.

- Available operands: `+`, `-`, `*` and `/`
- Example REQ frame to successful operation `1 2 3 * +`:
    - REQ frame: `0x0001 0x3B 0x01 0x3B 0x102030A0B0 0x24`
    - RES frame: `0x0001 0x3B 0x70 0x24`
- Example REQ frame to failes operation `1 + 2 * 3`:
    - REQ frame: `0x0001 0x3B 0x01 0x3B 0x10A020C030 0x24`
    - RES frame: `0x0001 0x3B 0xFF 0x24`

#### Bye
This frame close the connection with the server. The frame not have payload.

- Example REQ frame: `0x0010 0x3B 0x02 0x3B 0x24`
- Example RES frame: `0x0010 0x3B 0x428969 0x24`

#### Error frames:
For any unknown frame received on the server side produce a response with ERROR payload.

- Example REQ frame: `0x0012 0x10 0x20`
- Example RES frame: `0x0000 0x3B 0xEE 0x24`

