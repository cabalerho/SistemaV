package ventas.util;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.quartz.Job;

import modelo.hibernate.beans.DetalleVenta;
import modelo.hibernate.beans.Venta;

public class Impresion
{
	
	static SimpleDateFormat	formato	= new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	
	public static void ImpresionNota(Venta venta, String cliente, String domicilio)
	{
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pf = job.defaultPage();
		Paper paper = new Paper();
		double margin = 10;
		paper.setImageableArea(margin, margin, paper.getWidth() - margin, paper.getHeight() - margin);
		pf.setPaper(paper);
		pf.setOrientation(PageFormat.LANDSCAPE);
		job.setPrintable(new ObjetoImprimir(venta, cliente, domicilio), pf);
		job.setJobName("nombre_de_impresion");
		try
		{
			if (job.printDialog())
			{
				job.print();
			}
		}
		catch (PrinterException e)
		{
			System.out.println(e);
		}
		
	}
	
	public static void impresionTicket(Venta venta) throws FileNotFoundException, PrintException
	{
		String contentTicket = "Ferreteria Herco\nEXPEDIDO EN: Ahucatlan, Pue.\nDOMICILIO CONOCIDO AHUACATLAN.\nTEL. 764 76 3 32 53\n=============================\nRFC: HEPI8002008N99\n{{caja}} - Ticket # {{ticket}}\nLE ATENDIO: {{cajero}}\n{{dateTime}}\n=============================\n{{items}}\n=============================\nTOTAL: {{total}}\n\n{{Cliente}}=============================\nGRACIAS POR SU COMPRA...\nESPERAMOS SU VISITA NUEVAMENTE\n\n\n\n\n\n\n\n";
		
		contentTicket = contentTicket.replace("{{ticket}}", venta.getFiidventa() + "");
		contentTicket = contentTicket.replace("{{cajero}}", venta.getUsuario().getFcnombre() + " " + venta.getUsuario().getFcapepat() + " "
				+ venta.getUsuario().getFcapemat());
		contentTicket = contentTicket.replace("{{dateTime}}", formato.format(venta.getFdfechaventa()));
		
		String itmes = "";
		for (DetalleVenta detVe : venta.getDetalleVenta())
		{
			String nombre = detVe.getProducto().getFcnomproducto();
			String nombreF = "";
			for (int i = 0; i < nombre.length(); i += 16)
			{
				if (i + 16 < nombre.length())
				{
					nombreF = nombreF + nombre.substring(i, i + 16) + "\n";
				} else
				{
					nombreF = nombreF + agregarEspacion(nombre.substring(i), 16, false);
				}
			}
			itmes = itmes
					+ (detVe.getFicantidad() % 1.0D == 0.0D ? (int) detVe.getFicantidad() : new StringBuilder(String.valueOf(detVe.getFicantidad()))
							.toString()) + " " + nombreF + " "
					+ agregarEspacion(new StringBuilder(String.valueOf(detVe.getFdprecioventa())).toString(), 5, true) + " "
					+ agregarEspacion(new StringBuilder(String.valueOf(detVe.getFdsubtotal())).toString(), 5, true) + "\n";
		}
		contentTicket = contentTicket.replace("{{items}}", itmes);
		contentTicket = contentTicket.replace("{{total}}", venta.getFdtotal() + "");
		contentTicket = contentTicket.replace("{{caja}}", venta.getCaja().getFcdesccaja());
		if (venta.getCliente().getFiidcliente() > Constantes.IDDEDAULT.longValue())
		{
			contentTicket = contentTicket.replace("{{Cliente}}", venta.getCliente().getFcnombre() + " " + venta.getCliente().getFcapepat() + " "
					+ venta.getCliente().getFcapemat() + "\n");
		} else
		{
			contentTicket = contentTicket.replace("{{Cliente}}", "");
		}
		System.out.println(contentTicket);
		print(contentTicket);
	}
	
	public static void impresionTicket(Venta venta, String cambio, String recibido, String lblcambio, double diferencia)
			throws FileNotFoundException, PrintException
	{
		String contentTicket = "Ferreteria Herco\nEXPEDIDO EN: Ahucatlan, Pue.\nDOMICILIO CONOCIDO AHUACATLAN.\nTEL. 764 76 3 32 53\n=============================\nRFC: HEPI8002008N99\n{{caja}} - Ticket # {{ticket}}\nLE ATENDIO: {{cajero}}\n{{dateTime}}\n=============================\n{{items}}\n=============================\nTOTAL: {{total}}\n\n{{ventaanterior}}RECIBIDO: {{recibo}}\n{{lnlcambio}}: {{change}}\n\n{{Cliente}}=============================\nGRACIAS POR SU COMPRA...\nESPERAMOS SU VISITA NUEVAMENTE\n\n\n\n\n\n\n\n";
		
		contentTicket = contentTicket.replace("{{ticket}}", venta.getFiidventa() + "");
		contentTicket = contentTicket.replace("{{cajero}}", venta.getUsuario().getFcnombre() + " " + venta.getUsuario().getFcapepat() + " "
				+ venta.getUsuario().getFcapemat());
		contentTicket = contentTicket.replace("{{dateTime}}", formato.format(venta.getFdfechaventa()));
		String itmes = "";
		for (DetalleVenta detVe : venta.getDetalleVenta())
		{
			String nombre = detVe.getProducto().getFcnomproducto();
			String nombreF = "";
			for (int i = 0; i < nombre.length(); i += 16)
			{
				if (i + 16 < nombre.length())
				{
					nombreF = nombreF + nombre.substring(i, i + 16) + "\n";
				} else
				{
					nombreF = nombreF + agregarEspacion(nombre.substring(i), 16, false);
				}
			}
			itmes = itmes
					+ (detVe.getFicantidad() % 1.0D == 0.0D ? (int) detVe.getFicantidad() : new StringBuilder(String.valueOf(detVe.getFicantidad()))
							.toString()) + " " + nombreF + " "
					+ agregarEspacion(new StringBuilder(String.valueOf(detVe.getFdprecioventa())).toString(), 5, true) + " "
					+ agregarEspacion(new StringBuilder(String.valueOf(detVe.getFdsubtotal())).toString(), 5, true) + "\n";
		}
		contentTicket = contentTicket.replace("{{items}}", itmes);
		contentTicket = contentTicket.replace("{{total}}", venta.getFdtotal() + "");
		contentTicket = contentTicket.replace("{{change}}", cambio);
		contentTicket = contentTicket.replace("{{recibo}}", recibido);
		contentTicket = contentTicket.replace("{{caja}}", venta.getCaja().getFcdesccaja());
		if (venta.getCliente().getFiidcliente() > Constantes.IDDEDAULT.longValue())
		{
			String desc = "";
			 if (venta.getCliente().getFddescuento() > 0.0D) {
		         desc = "Descuento: " + venta.getCliente().getFddescuento() + "\n";
		        }
			
			contentTicket = contentTicket.replace("{{Cliente}}", venta.getCliente().getFcnombre() + " " + venta.getCliente().getFcapepat() + " "
					+ venta.getCliente().getFcapemat() + "\n" + desc);
			
		} else
		{
			contentTicket = contentTicket.replace("{{Cliente}}", "");
		}
		contentTicket = contentTicket.replace("{{lnlcambio}}", lblcambio);
		String ventaAnterior = "";
		if (venta.getFiidventaanterior() > 0L)
		{
			ventaAnterior = "Venta Anterior: " + venta.getFiidventaanterior() + "\n";
			ventaAnterior = ventaAnterior + "Diferencia entra ventas: " + diferencia + "\n";
		}
		contentTicket = contentTicket.replace("{{ventaanterior}}", ventaAnterior);
		
		print(contentTicket);
	}
	
	public static void print(String contentTicket) throws PrintException, FileNotFoundException
	{
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService service = null;
		
		for (PrintService s : services)
		{
			String impresora = s.toString();
			if (impresora.indexOf("RP58") != -1)
			{
				System.out.println(s);
				service = s;
			}
		}
		
		//service = ServiceUI.printDialog(null, 700, 200, services, defaultService, flavor, pras);
		
		byte[] bytes = contentTicket.getBytes();
		System.out.println(contentTicket);
		
		if (service != null)
		{
			DocPrintJob job = service.createPrintJob();
			Object doc2 = new SimpleDoc(bytes, flavor, null);
			job.print((Doc) doc2, null);
		}
	}
	
	private static String agregarEspacion(String valor, int cantidad, boolean inicio)
	{
		while (valor.length() < cantidad)
		{
			if (inicio)
			{
				valor = " " + valor;
			} else
			{
				valor = valor + " ";
			}
		}
		if (valor.length() > cantidad)
		{
			valor.substring(0, cantidad);
		}
		return valor;
	}
	
}
