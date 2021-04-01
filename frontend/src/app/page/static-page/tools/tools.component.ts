import { Component, OnInit } from '@angular/core';
import { Constants } from 'src/app/constants';
import saveAs from 'save-as';
import { DomSanitizer } from '@angular/platform-browser';
import { StaticServiceService } from '../services/static-service.service';
declare var $:any;

@Component({
  selector: 'rmncha-tools',
  templateUrl: './tools.component.html',
  styleUrls: ['./tools.component.scss']
})
export class ToolsComponent implements OnInit {

  tableData: any;
  tableColumns: string[];
  title;
  url;
  constructor(private staticService: StaticServiceService,private domSanizitizer :DomSanitizer) { }

  ngOnInit() {
    let data = {
      mainMenu: "Resources",
      subMenu: "Tools"
    }
    this.staticService.getCMSRequestDataInTable(data).subscribe(data =>{
    this.tableData = data;
  })
  }
  tableActionClicked(rowObj){
    if (rowObj.target.includes('preview')) {
      this.title=rowObj.rowObj.q3[0].originalName;
      let filepath = rowObj.rowObj.q3[0].filePath
      this.url=this.domSanizitizer.bypassSecurityTrustResourceUrl( Constants.API_GATE_WAY+Constants.SERVICE_GATE_WAY+Constants.DASHBOARD_SERVICE_URL+'anynomus/getFile?fileName='+filepath)
      $("#myModal").modal("show");
    }
    else if(rowObj.target.includes('download'))
    {
      let filepath = rowObj.rowObj.q3[0].filePath
      let actualName = rowObj.rowObj.q3[0].originalName
      this.staticService.downloadFile(filepath).subscribe(d=>{
        saveAs(d, actualName);
      })
    }
  }

}
