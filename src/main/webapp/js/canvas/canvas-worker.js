// 웹 워커 안에서는 window 객체를 쓸 수 없으므로,
// importScripts를 사용해 pako 라이브러리를 직접 로드
self.importScripts(
  "https://cdnjs.cloudflare.com/ajax/libs/pako/2.1.0/pako.min.js"
);

// 메인 스레드로부터 메시지를 수신하는 리스너입니다.
self.onmessage = (event) => {
  const { type, data } = event.data;

  try {
    if (type === "compress") {
      // 1. 압축 요청 처리
      const jsonString = JSON.stringify(data);
      const compressedData = pako.deflate(jsonString);
      // ArrayBuffer를 Base64 문자열로 변환하는 로직
      const base64String = btoa(
        String.fromCharCode.apply(null, compressedData)
      );

      // 결과를 메인 스레드로 다시 보냅니다.
      self.postMessage({ type: "compressed", result: base64String });
    } else if (type === "decompress") {
      // 2. 압축 해제 요청 처리
      let m;
      try {
        // 먼저 일반 JSON 메시지인지 파싱 시도
        m = JSON.parse(data);
      } catch (e) {
        // JSON 파싱 실패 시, Base64 디코딩 및 압축 해제 시도
        try {
          const binaryString = atob(data);
          const len = binaryString.length;
          const bytes = new Uint8Array(len);
          for (let i = 0; i < len; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }
          const decompressedData = pako.inflate(bytes, { to: "string" });
          m = JSON.parse(decompressedData);
        } catch (decodeError) {
          // 두 시도 모두 실패하면 오류 전송
          self.postMessage({
            type: "error",
            message:
              "Failed to decode or decompress data: " + decodeError.message,
          });
          return;
        }
      }
      // 성공적으로 처리된 메시지를 메인 스레드로 전송
      self.postMessage({ type: "decompressed", result: m });
    }
  } catch (error) {
    // 오류 발생 시 메인 스레드로 오류 메시지를 보냅니다.
    self.postMessage({ type: "error", message: error.message });
  }
};
