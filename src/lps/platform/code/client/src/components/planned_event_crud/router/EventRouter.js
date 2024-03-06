/*% if (feature.MWM_VisitSchedule) { %*/
import EventList from "../components/EventList";
import EventForm from "../components/EventForm";
import EventDetail from "../components/EventDetail";

const routes = [
  {
    path: "/events",
    name: "EventList",
    meta: {
      label: "planned_event_crud.visitList",
    },
    component: EventList,
  },
  {
    path: "/events/:id/edit",
    name: "EventForm",
    meta: {
      label: "planned_event_crud.visitForm",
    },
    component: EventForm,
  },
  {
    path: "/events/new",
    name: "EventCreate",
    meta: {
      label: "planned_event_crud.visitForm",
    },
    component: EventForm,
  },
  {
    path: "/events/:id(\\d+)",
    name: "EventDetail",
    meta: {
      label: "planned_event_crud.visitDetail",
    },
    component: EventDetail,
  },
];

export default routes;
/*% } %*/
