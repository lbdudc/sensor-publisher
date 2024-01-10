/*% if (feature.MWM_VisitSchedule) { %*/
/*% if (feature.MVM_VT_OnlyDay) { %*/
import EventCalendar from "./EventCalendar.vue"
/*% } %*/
/*% if (feature.MVM_VT_WithTime) { %*/
import EventCalendarWithTime from "./EventCalendarWithTime"
/*% } %*/

const routes = [
    /*% if (feature.MVM_VT_OnlyDay) { %*/
{
    path: "/event-calendar",
    name: "EventCalendar",
    meta: {
      label: "event_calendar.eventCalendar"
    },
    component: EventCalendar
},
/*% } %*/
/*% if (feature.MVM_VT_WithTime) { %*/
{
    path: "/event-calendar",
    name: "EventCalendar",
    meta: {
      label: "event_calendar.eventCalendar"
    },
    component: EventCalendarWithTime
}
    /*% } %*/
]

export default routes;
/*% } %*/
