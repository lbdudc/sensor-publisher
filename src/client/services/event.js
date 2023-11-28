// from https://stackoverflow.com/questions/21817397/event-handler-namespace-in-vanilla-javascript
class EventClass {
  constructor() {
    this.functionMap = {};
  }

  addEventListener(event, func) {
    this.functionMap[event] = func;
    document.addEventListener(event.split(".")[0], this.functionMap[event]);
  }

  removeEventListener(event) {
    document.removeEventListener(event.split(".")[0], this.functionMap[event]);
    delete this.functionMap[event];
  }
}

const Event = new EventClass();
export default Event;
